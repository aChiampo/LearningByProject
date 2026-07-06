package com.WW.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
    public VisitaService(VisitaRepository visitaRepository) {
        this.visitaRepository = visitaRepository;
    }

    /**
     * Crea una nuova visita.
     *
     * @param visita dati della visita da salvare
     * @return visita creata
     */
    @Transactional
    public Visita createVisita(Visita visita) {
        return visitaRepository.save(visita);
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
     * Restituisce le visite che risultano pagate, cioè collegate a un pagamento.
     *
     * @return visite pagate
     */
    @Transactional(readOnly = true)
    public List<Visita> getVisitePagate() {
        return visitaRepository.findByPagamentoIsNotNull();
    }

    /**
     * Restituisce le visite che non hanno ancora un pagamento associato.
     *
     * @return visite non pagate
     */
    @Transactional(readOnly = true)
    public List<Visita> getVisiteNonPagate() {
        return visitaRepository.findByPagamentoIsNull();
    }

    /**
     * Aggiorna lo stato di pagamento di una visita.
     *
     * @param id identificativo della visita
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
     * Aggiorna le note di una visita e salva le modifiche nel database.
     *
     * @param visita visita da aggiornare
     * @param note   nuove note
     * @return la visita aggiornata
     */
    @Transactional
    public Visita updateVisitaNote(Visita visita, String note) {
        visita.setNote(note);
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
        return updateVisitaNote(getVisitaById(id), note);
    }

    /**
     * Aggiorna lo stato di pagamento di una visita e salva le modifiche nel
     * database.
     *
     * @param visita visita da aggiornare
     * @param pagato nuovo stato di pagamento
     * @return la visita aggiornata
     */

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

}
