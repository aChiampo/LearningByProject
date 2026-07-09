package com.WW.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.WW.dto.VisitaDto;
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
    private final TipoVisitaService tipoVisitaService;
    private final UtenteService utenteService;
    private final AnimaleService animaleService;
    private final PagamentiService pagamentiService;

    /**
     * Costruisce il servizio con il repository delle visite.
     *
     * @param visitaRepository repository delle visite
     */
    public VisitaService(
            VisitaRepository visitaRepository,
            TipoVisitaService tipoVisitaService,
            UtenteService utenteService,
            AnimaleService animaleService,
            PagamentiService pagamentiService) {
        this.visitaRepository = visitaRepository;
        this.tipoVisitaService = tipoVisitaService;
        this.utenteService = utenteService;
        this.animaleService = animaleService;
        this.pagamentiService = pagamentiService;
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

    private void validaVisitaPerCreazione(VisitaDto visita) {
        if (visita == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il corpo della visita non può essere nullo.");
        }

        if (visita.dataVisita() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La data della visita è obbligatoria.");
        }

        if (visita.tipoVisita() == null || visita.tipoVisita().id() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il tipo visita è obbligatorio e deve contenere un id valido.");
        }

        if (visita.animale() == null || visita.animale().id() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'animale è obbligatorio e deve contenere un id valido.");
        }

        if (visita.veterinario() == null || visita.veterinario().id() == null) {
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
        entity.setDataVisita(visita.dataVisita());
        entity.setTipoVisita(tipoVisitaService.ottieniPerId(visita.tipoVisita().id()));
        entity.setAnimale(animaleService.ottieniPerId(visita.animale().id()));
        entity.setVeterinario(utenteService.ottieniPerId(visita.veterinario().id()));
        if (visita.pagamento() != null) {
            entity.setPagamento(pagamentiService.ottieniPerId(visita.pagamento().id()));
        }
        entity.setNote(visita.note());
        return entity;
    }

    /**
     * Restituisce tutte le visite.
     *
     * @return elenco delle visite
     */
    @Transactional(readOnly = true)
    public List<Visita> getAllVisita() {
        return visitaRepository.findAll();
    }

    /**
     * Restituisce una visita a partire dall'id.
     *
     * @param id identificativo della visita
     * @return visita trovata
     */
    @Transactional(readOnly = true)
    public Visita getVisitaById(Integer id) {
        return visitaRepository.findById(id)
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
        return visitaRepository.findByTipoVisitaId(idTipoVisita);
    }

    /**
     * Restituisce le visite associate a un veterinario.
     *
     * @param idVeterinario identificativo del veterinario
     * @return visite filtrate
     */
    @Transactional(readOnly = true)
    public List<Visita> getVisiteByVeterinario(Integer idVeterinario) {
        return visitaRepository.findByVeterinarioId(idVeterinario);
    }

    /**
     * Restituisce le visite associate a un animale.
     *
     * @param idAnimale identificativo dell'animale
     * @return visite filtrate
     */
    @Transactional(readOnly = true)
    public List<Visita> getVisiteByAnimale(Integer idAnimale) {
        return visitaRepository.findByAnimaleId(idAnimale);
    }

    /**
     * Restituisce la visita collegata a un pagamento.
     *
     * @param idPagamento identificativo del pagamento
     * @return visita trovata
     */
    @Transactional(readOnly = true)
    public Visita getVisitaByPagamento(Integer idPagamento) {
        if (idPagamento == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'identificativo del pagamento non può essere nullo.");
        }
        return visitaRepository.findByPagamentoId(idPagamento)
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
    public Visita updateVisitaPagato(Integer id, boolean pagato) {
        Visita visita = getVisitaById(id);

        if (!pagato) {
            visita.setPagamento(null);
            return visitaRepository.save(visita);
        }

        if (visita.getPagamento() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Per marcare la visita come pagata è necessario associare prima un pagamento.");
        }

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
        if (!visitaRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Impossibile eliminare: Visita non trovata.");
        }
        visitaRepository.deleteById(id);
    }

    /**
     * Restituisce tutte le visite che risultano pagate.
     *
     * @return elenco delle visite pagate
     */
    public List<Visita> OttieniVisitePagate() {

        List<Visita> pagate = visitaRepository.findAll().stream()
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
        List<Visita> nonPagate = visitaRepository.findAll().stream()
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

        List<Visita> pagate = visitaRepository.findAll().stream()
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
        List<Visita> nonPagate = visitaRepository.findAll().stream()
                .filter(visita -> visita.getPagamento() == null)
                .filter(visita -> visita.getAnimale().getId() == idAnimale)
                .toList();

        return nonPagate;
    }

}
