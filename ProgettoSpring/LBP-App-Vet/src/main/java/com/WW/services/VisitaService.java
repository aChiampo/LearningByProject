package com.WW.services;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.WW.dto.PagamentoDto;
import com.WW.dto.VisitParamDTO;
import com.WW.dto.VisitaDto;
import com.WW.entities.Pagamento;
import com.WW.entities.TipoVisita;
import com.WW.entities.Utente;
import com.WW.entities.Visita;
import com.WW.repositories.VisitaRepository;

/**
 * Servizio per la tabella VISITA.
 */
@Service
public class VisitaService {

    private final VisitaRepository visitaRepository;
    private final TipoVisitaService tipoVisitaService;
    private final AnimaleService animaleService;
    private final UtenteService utenteService;
    private final PagamentiService pagamentiService;

    public VisitaService(
            VisitaRepository visitaRepository, TipoVisitaService tipoVisitaService, AnimaleService animaleService,
            UtenteService utenteService, PagamentiService pagamentiService) {
        this.visitaRepository = visitaRepository;
        this.tipoVisitaService = tipoVisitaService;
        this.animaleService = animaleService;
        this.utenteService = utenteService;
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il corpo della visita non puo essere nullo.");
        }

        validaSovrapposizioneOrario(visita.getStartDateTime(), tipoVisitaService.ottieniPerId(visita.tipoVisita().id()),
                utenteService.ottieniPerId(visita.veterinario().id()));

        if (visita.tipoVisita() == null || visita.tipoVisita() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il tipo visita è obbligatorio e deve contenere un id valido.");
        }
        if (visita.animale() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'animale e obbligatorio.");
        }
        if (visita.veterinario() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il veterinario e obbligatorio.");
        }
        if (visita.pagamento() != null && visita.pagamento().id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il pagamento deve contenere un id valido.");
        }

        validaSovrapposizioneOrario(
                visita.getStartDateTime(),
                tipoVisitaService.ottieniPerId(visita.tipoVisita()),
                visita.veterinario());
    }

    private Visita toEntity(VisitaDto visita) {
        Visita entity = new Visita();
        entity.setAnimale(animaleService.ottieniPerId(visita.animale().id()));
        entity.setTipoVisita(tipoVisitaService.ottieniPerId(visita.tipoVisita().id()));
        entity.setVeterinario(utenteService.ottieniPerId(visita.veterinario().id()));
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
    private void validaSovrapposizioneOrario(LocalDateTime dataVisita, TipoVisita tipoVisita, Utente veterinario) {
        LocalDateTime nuovaInizio = dataVisita;
        LocalDateTime nuovaFine = dataVisita.plusMinutes(tipoVisita.getDurata());
        LocalDateTime windowStart = dataVisita.minusHours(6);
        LocalDateTime windowEnd = nuovaFine.plusHours(6);

        List<Visita> candidate = visitaRepository.findVisiteVeterinarioNelPeriodo(
                veterinario.getId(), windowStart, windowEnd);

        for (Visita esistente : candidate) {
            LocalDateTime esistenteInizio = esistente.getDataVisita();
            LocalDateTime esistenteFine = esistenteInizio.plusMinutes(esistente.getTipoVisita().getDurata());
            boolean sovrapposte = nuovaInizio.isBefore(esistenteFine) && esistenteInizio.isBefore(nuovaFine);

            if (sovrapposte) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Il veterinario ha gia una visita prenotata in questo intervallo orario.");
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Visita> getAllVisita() {
        return visitaRepository.findByIsDeletedFalse();
    }

    @Transactional(readOnly = true)
    public List<Visita> getAllVisita(Integer clienteId) {
        if (clienteId == null) {
            return visitaRepository.findByIsDeletedFalse();
        }

        return visitaRepository.findByAnimaleUtenteIdAndIsDeletedFalse(clienteId);
    }

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

    /**
     * Converte un'entità Visita in un DTO VisitaDto.
     * 
     * @param visita entità Visita da convertire
     * @return DTO VisitaDto corrispondente
     */
    public VisitaDto toDto(Visita visita) {
        return VisitaDto.fromEntity(visita);
    }

    private String toFasciaOraria(LocalDateTime dataVisita) {
        if (dataVisita == null) {
            return null;
        }

        return dataVisita.getHour() < 13
                ? "Mattina (09:00 - 12:30)"
                : "Pomeriggio (14:30 - 18:30)";
    }

    @Transactional(readOnly = true)
    public Visita getVisitaById(Integer id) {
        return visitaRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visita non trovata."));
    }

    @Transactional(readOnly = true)
    public List<Visita> getVisiteByTipoVisita(Integer idTipoVisita) {
        return visitaRepository.findByTipoVisitaIdAndIsDeletedFalse(idTipoVisita);
    }

    @Transactional(readOnly = true)
    public List<Visita> getVisiteByVeterinario(Integer idVeterinario) {
        return visitaRepository.findByVeterinarioIdAndIsDeletedFalse(idVeterinario);
    }

    @Transactional(readOnly = true)
    public List<Visita> getVisiteByAnimale(Integer idAnimale) {
        return visitaRepository.findByAnimaleIdAndIsDeletedFalse(idAnimale);
    }

    @Transactional(readOnly = true)
    public Visita getVisitaByPagamento(Integer idPagamento) {
        return visitaRepository.findByPagamentoIdAndIsDeletedFalse(idPagamento)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Visita collegata al pagamento non trovata."));
    }

    @Transactional
    public Visita updateVisitaPagato(Integer id, Integer pagamentoId) {
        Visita visita = getVisitaById(id);
        Pagamento pagato = pagamentiService.ottieniPerId(pagamentoId);
        visita.setPagamento(pagato);
        return visitaRepository.save(visita);
    }

    @Transactional
    public Visita updateVisitaNote(Integer id, String note) {
        Visita visita = getVisitaById(id);
        visita.setNote(note);
        return visitaRepository.save(visita);
    }

    @Transactional
    public void deleteVisita(Integer id) {
        Visita visita = visitaRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Impossibile eliminare: Visita non trovata."));
        visita.setIsDeleted(true);
        visitaRepository.save(visita);
    }

    @Transactional(readOnly = true)
    public List<Visita> OttieniVisitePagate() {
        return OttieniVisitePagate(null);
    }

    @Transactional(readOnly = true)
    public List<Visita> OttieniVisitePagate(Integer clienteId) {
        if (clienteId == null) {
            return visitaRepository.findByPagamentoIsNotNullAndIsDeletedFalse();
        }

        return visitaRepository.findByPagamentoIsNotNullAndAnimaleUtenteIdAndIsDeletedFalse(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Visita> OttieniVisiteNonPagate() {
        return OttieniVisiteNonPagate(null);
    }

    @Transactional(readOnly = true)
    public List<Visita> OttieniVisiteNonPagate(Integer clienteId) {
        if (clienteId == null) {
            return visitaRepository.findByPagamentoIsNullAndIsDeletedFalse();
        }

        return visitaRepository.findByPagamentoIsNullAndAnimaleUtenteIdAndIsDeletedFalse(clienteId);
    }

    public List<Visita> OttieniVisitePagatebyAnimale(int idAnimale) {
        return visitaRepository.findByIsDeletedFalse().stream()
                .filter(visita -> visita.getPagamento() != null)
                .filter(visita -> visita.getAnimale().getId() == idAnimale)
                .toList();
    }

    public List<Visita> OttieniVisiteNonPagateByAnimale(int idAnimale) {
        return visitaRepository.findByIsDeletedFalse().stream()
                .filter(visita -> visita.getPagamento() == null)
                .filter(visita -> visita.getAnimale().getId() == idAnimale)
                .toList();
    }

    public Visita ottieniVisitaByData(LocalDateTime dataVisita) {
        return visitaRepository.findByDataVisitaAndIsDeletedFalse(dataVisita)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Visita non trovata per la data specificata."));
    }
}
