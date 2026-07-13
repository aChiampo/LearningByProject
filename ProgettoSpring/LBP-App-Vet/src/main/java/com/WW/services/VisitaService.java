package com.WW.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.WW.dto.AnimaleDto;
import com.WW.dto.PagamentoDto;
import com.WW.dto.TipoVisitaDto;
import com.WW.dto.UtenteDto;
import com.WW.dto.VisitParamDTO;
import com.WW.dto.VisitaDto;
import com.WW.entities.Pagamento;
import com.WW.entities.TipoVisita;
import com.WW.entities.Visita;
import com.WW.repositories.VisitaRepository;

/**
 * @author: cristian.pappalardo
 *          Servizio per la tabella VISITA
 *          Last update: 28/06/2026
 */
@Service
public class VisitaService {

    private final VisitaRepository visitaRepository;

    /**
     * Costruisce il servizio con il repository delle visite.
     *
     * @param visitaRepository repository delle visite
     */
    public VisitaService(
            VisitaRepository visitaRepository) {
        this.visitaRepository = visitaRepository;
    }

    /**
     * Crea una nuova visita.
     *
     * @param visita dati della visita da salvare
     * @return visita creata
     */
    @Transactional
    public Visita createVisita(VisitaDto visita) {
        validaVisitaPerCreazione(visita);
        return visitaRepository.save(toEntity(visita));
    }

    /**
     * Valida i dati della visita prima della creazione.
     * 
     * @param visita
     * @throws ResponseStatusException se i dati della visita non sono validi
     */
    private void validaVisitaPerCreazione(VisitaDto visita) {
        if (visita == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il corpo della visita non può essere nullo.");
        }

        validaSovrapposizioneOrario(visita.getStartDateTime(), tipoVisitaService.ottieniPerId(visita.tipoVisita()),
                visita.veterinario());

        if (visita.tipoVisita() == null || visita.tipoVisita() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il tipo visita è obbligatorio e deve contenere un id valido.");
        }

        if (visita.animale() == null || visita.animale() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'animale è obbligatorio e deve contenere un id valido.");
        }

        if (visita.veterinario() == null || visita.veterinario() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il veterinario è obbligatorio e deve contenere un id valido.");
        }

        if (visita.pagamento() != null && visita.pagamento().id() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Se presente, il pagamento deve contenere un id valido.");
        }
    }

    private Visita toEntity(VisitaDto visita) {
        Visita entity = new Visita();
        entity.setAnimale(animaleService.ottieniPerId(visita.animale()));
        entity.setTipoVisita(tipoVisitaService.ottieniPerId(visita.tipoVisita()));
        entity.setVeterinario(utenteService.ottieniPerId(visita.veterinario()));
        entity.setDataVisita(visita.getStartDateTime());
        if (visita.pagamento() != null) {
            entity.setPagamento(pagamentiService.ottieniPerId(visita.pagamento().id()));
        }
        entity.setNote(visita.note());
        entity.setIsDeleted(false);
        return entity;
    }

    /**
     * Verifica che l'orario richiesto per la nuova visita non si sovrapponga
     * a un'altra visita già prenotata per lo stesso veterinario.
     *
     * @param dataVisita    data/ora di inizio della nuova visita
     * @param tipoVisita    tipo di visita richiesto (fornisce la durata)
     * @param veterinarioId id del veterinario
     * @throws ResponseStatusException se l'orario richiesto si sovrappone a una
     *                                 visita esistente
     */
    private void validaSovrapposizioneOrario(LocalDateTime dataVisita, TipoVisita tipoVisita, int veterinarioId) {
        LocalDateTime nuovaInizio = dataVisita;
        LocalDateTime nuovaFine = dataVisita.plusMinutes(tipoVisita.getDurata());

        // Finestra di ricerca abbondante: nessuna visita esistente più lunga
        // di qualche ora dovrebbe sfuggire a questo intervallo.
        LocalDateTime windowStart = dataVisita.minusHours(6);
        LocalDateTime windowEnd = nuovaFine.plusHours(6);

        List<Visita> candidate = visitaRepository.findVisiteVeterinarioNelPeriodo(
                veterinarioId, windowStart, windowEnd);

        for (Visita esistente : candidate) {
            LocalDateTime esistenteInizio = esistente.getDataVisita();
            LocalDateTime esistenteFine = esistenteInizio.plusMinutes(esistente.getTipoVisita().getDurata());

            // due intervalli si sovrappongono se: inizioA < fineB AND inizioB < fineA
            boolean sovrapposte = nuovaInizio.isBefore(esistenteFine) && esistenteInizio.isBefore(nuovaFine);

            if (sovrapposte) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Il veterinario ha già una visita prenotata in questo intervallo orario.");
            }
        }
    }

    /**
     * Restituisce tutte le visite.
     *
     * @return elenco delle visite
     */
    @Transactional(readOnly = true)
    public List<Visita> getAllVisita() {
        return visitaRepository.findByIsDeletedFalse();
    }

    /**
     * Restituisce le visite filtrate dai parametri specificati.
     *
     * @param params parametri opzionali di filtro
     * @return visite filtrate convertite in DTO
     */
    @Transactional(readOnly = true)
    public List<VisitaDto> getVisiteByParams(VisitParamDTO params) {
        return getAllVisita().stream()
                .filter(visita -> params == null || params.date() == null
                        || !visita.getDataVisita().isBefore(params.date()))
                .filter(visita -> params == null || params.doctorID() == null
                        || Objects.equals(visita.getVeterinario().getId(), params.doctorID()))
                .filter(visita -> params == null || params.clientID() == null
                        || Objects.equals(visita.getAnimale().getUtente().getId(), params.clientID()))
                .filter(visita -> params == null || params.animalID() == null
                        || Objects.equals(visita.getAnimale().getId(), params.animalID()))
                .filter(visita -> params == null || params.tipoVisitaID() == null
                        || Objects.equals(visita.getTipoVisita().getId(), params.tipoVisitaID()))
                .filter(visita -> params == null || params.pagamentoID() == null
                        || visita.getPagamento() != null
                                && Objects.equals(visita.getPagamento().getId(), params.pagamentoID()))
                .filter(visita -> params == null || params.stato() == null
                        || visita.getStato() == params.stato())
                .filter(visita -> params == null || params.pagata() == null
                        || Objects.equals(visita.getPagamento() != null, params.pagata()))
                .map(this::toDto)
                .toList();
    }

    private VisitaDto toDto(Visita visita) {
        return new VisitaDto(
                visita.getDataVisita(),
                visita.getTipoVisita() == null ? null : new TipoVisitaDto(visita.getTipoVisita().getId()),
                visita.getAnimale() == null ? null : new AnimaleDto(visita.getAnimale().getId()),
                visita.getVeterinario() == null ? null : new UtenteDto(visita.getVeterinario().getId()),
                visita.getPagamento() == null ? null : new PagamentoDto(visita.getPagamento().getId()),
                visita.getNote());
    }

    /**
     * Restituisce una visita a partire dall'id.
     *
     * @param id identificativo della visita
     * @return visita trovata
     */
    @Transactional(readOnly = true)
    public Visita getVisitaById(Integer id) {
        return visitaRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Visita non trovata."));
    }

    /**
     * Restituisce le visite associate a un tipo visita.
     *
     * @param idTipoVisita identificativo del tipo visita
     * @return visite filtrate
     */
    @Transactional(readOnly = true)
    public List<Visita> getVisiteByTipoVisita(Integer idTipoVisita) {
        return visitaRepository.findByTipoVisitaIdAndIsDeletedFalse(idTipoVisita);
    }

    /**
     * Restituisce le visite associate a un veterinario.
     *
     * @param idVeterinario identificativo del veterinario
     * @return visite filtrate
     */
    @Transactional(readOnly = true)
    public List<Visita> getVisiteByVeterinario(Integer idVeterinario) {
        return visitaRepository.findByVeterinarioIdAndIsDeletedFalse(idVeterinario);
    }

    /**
     * Restituisce le visite associate a un animale.
     *
     * @param idAnimale identificativo dell'animale
     * @return visite filtrate
     */
    @Transactional(readOnly = true)
    public List<Visita> getVisiteByAnimale(Integer idAnimale) {
        return visitaRepository.findByAnimaleIdAndIsDeletedFalse(idAnimale);
    }

    /**
     * Restituisce la visita collegata a un pagamento.
     *
     * @param idPagamento identificativo del pagamento
     * @return visita trovata
     */
    @Transactional(readOnly = true)
    public Visita getVisitaByPagamento(Integer idPagamento) {

        return visitaRepository.findByPagamentoIdAndIsDeletedFalse(idPagamento)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Visita collegata al pagamento non trovata."));
    }

    /**
     * Aggiorna lo stato di pagamento di una visita.
     *
     * @param id     identificativo della visita
     * @param pagato nuovo stato di pagamento
     * @return visita aggiornata
     */
    @Transactional
    public Visita updateVisitaPagato(Integer id, Integer pagamentoId) {
        Visita visita = getVisitaById(id);
        Pagamento pagato = pagamentiService.ottieniPerId(pagamentoId);
        visita.setPagamento(pagato);
        return visitaRepository.save(visita);
    }

    /**
     * Aggiorna le note di una visita a partire dall'id.
     *
     * @param id   identificativo della visita
     * @param note nuove note
     * @return visita aggiornata
     */
    @Transactional
    public Visita updateVisitaNote(Integer id, String note) {
        Visita visita = getVisitaById(id);
        visita.setNote(note);
        return visitaRepository.save(visita);
    }

    /**
     * Elimina una visita esistente.
     *
     * @param id identificativo della visita da eliminare
     */
    @Transactional
    public void deleteVisita(Integer id) {
        Visita visita = visitaRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Impossibile eliminare: Visita non trovata."));
        visita.setIsDeleted(true);
        visitaRepository.save(visita);
    }

    /**
     * Restituisce tutte le visite che risultano pagate.
     *
     * @return elenco delle visite pagate
     */
    public List<Visita> OttieniVisitePagate() {

        List<Visita> pagate = visitaRepository.findByIsDeletedFalse().stream()
                .filter(visita -> visita.getPagamento() != null)
                .toList();

        return pagate;
    }

    /**
     * Restituisce tutte le visite che risultano non pagate.
     *
     * @return elenco delle visite non pagate
     */
    public List<Visita> OttieniVisiteNonPagate() {
        List<Visita> nonPagate = visitaRepository.findByIsDeletedFalse().stream()
                .filter(visita -> visita.getPagamento() == null)
                .toList();

        return nonPagate;
    }

    /**
     * Restituisce tutte le visite che risultano pagate.
     * 
     * @param idAnimale identificativo dell'Animale
     * 
     * @return elenco delle visite pagate
     */
    public List<Visita> OttieniVisitePagatebyAnimale(int idAnimale) {

        List<Visita> pagate = visitaRepository.findByIsDeletedFalse().stream()
                .filter(visita -> visita.getPagamento() != null)
                .filter(visita -> visita.getAnimale().getId() == idAnimale)
                .toList();

        return pagate;
    }

    /**
     * Restituisce tutte le visite che risultano non pagate.
     * 
     * @param idAnimale identificativo dell'Animale
     * 
     * @return elenco delle visite non pagate
     */
    public List<Visita> OttieniVisiteNonPagateByAnimale(int idAnimale) {
        List<Visita> nonPagate = visitaRepository.findByIsDeletedFalse().stream()
                .filter(visita -> visita.getPagamento() == null)
                .filter(visita -> visita.getAnimale().getId() == idAnimale)
                .toList();

        return nonPagate;
    }

    public Visita ottieniVisitaByData(LocalDateTime dataVisita) {
        return visitaRepository.findByDataVisitaAndIsDeletedFalse(dataVisita)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Visita non trovata per la data specificata."));
    }

}
