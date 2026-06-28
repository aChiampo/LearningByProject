package com.WW.services;

import org.springframework.stereotype.Service;

import com.WW.entities.Visita;
import com.WW.repositories.VisitaRepository;

/**
 * @author: cristian.pappalardo
 * Modello di servizio per la tabella VISITA
 * Last update: 28/06/2026
 */
@Service
public class VisitaService {

    private final VisitaRepository visitaRepository;

    public VisitaService(VisitaRepository visitaRepository) {
        this.visitaRepository = visitaRepository;
    }

    public Visita createVisita(Visita visita) {
        return visitaRepository.save(visita);
    }

    public Visita getVisitaById(Integer id) {
        return visitaRepository.findById(id).orElse(null);
    }

    /**
     * Aggiorna le note di una visita e salva le modifiche nel database.
     * @param visita
     * @param note
     * @return la visita aggiornata
     */
    public Visita updateVisitaNote(Visita visita, String note) {
        visita.setNote(note);
        return visitaRepository.save(visita);
    }

    /**
     * Aggiorna lo stato di pagamento di una visita e salva le modifiche nel
     * database.
     * 
     * @param visita
     * @param pagato
     * @return la visita aggiornata
     */
    public Visita updateVisitaPagato(Visita visita, boolean pagato) {
        visita.setPagato(pagato);
        return visitaRepository.save(visita);
    }

    public void deleteVisita(Integer id) {
        visitaRepository.deleteById(id);
    }

}
