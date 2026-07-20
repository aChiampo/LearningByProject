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
import com.WW.dto.input.VisitaInputDTO;
import com.WW.dto.output.VisitaOutputDTO;
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
    public VisitaOutputDTO createVisita(VisitaInputDTO visita) {
        // Converte il DTO in entità e salva la visita
        Visita savedVisita = visitaRepository.save(Visita.fromDto(visita));

        // Converte l'entità salvata in DTO e restituisce il risultato
        VisitaOutputDTO visitaOutputDTO = VisitaOutputDTO.fromEntity(savedVisita);

        return visitaOutputDTO;
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
    public List<VisitaInputDTO> getVisiteByParams(VisitParamDTO params) {
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
