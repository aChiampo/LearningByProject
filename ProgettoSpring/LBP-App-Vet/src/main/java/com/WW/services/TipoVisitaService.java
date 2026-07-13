package com.WW.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.WW.entities.TipoVisita;
import com.WW.repositories.TipoVisitaRepo;

import lombok.RequiredArgsConstructor;

/**
 * Servizio per la gestione dei tipi visita.
 */
@Service
@RequiredArgsConstructor
public class TipoVisitaService {

    private final TipoVisitaRepo tipoVisitaRepo;

    @Transactional
    /**
     * Salva un nuovo tipo visita.
     *
     * @param tipoVisite dati del tipo visita da salvare
     * @return tipo visita creato
     */
    public TipoVisita aggiungiTipoVisite(TipoVisita tipoVisite) {
        if (tipoVisite.getIsDeleted() == null) {
            tipoVisite.setIsDeleted(false);
        }
        return tipoVisitaRepo.save(tipoVisite);
    }
    
    @Transactional

    /**
     * Aggiorna un tipo visita esistente.
     *
     * @param id identificativo del tipo visita da aggiornare
     * @param modificato nuovi dati da applicare
     * @return tipo visita aggiornato
     */
    public TipoVisita modificaTipoVisite(Integer id, TipoVisita modificato) {
        TipoVisita originale = ottieniPerId(id);

        if (modificato.getNome() != null && !modificato.getNome().isBlank()) {
            originale.setNome(modificato.getNome());
        }
        if (modificato.getDurata() > 0) {
            originale.setDurata(modificato.getDurata());
        }
        if (modificato.getCategoria() != null) {
            originale.setCategoria(modificato.getCategoria());
        }
        if (modificato.getPrezzo() != null) {
            originale.setPrezzo(modificato.getPrezzo());
        }
        if (modificato.getDottore() != null) {
            originale.setDottore(modificato.getDottore());
        }
        originale.setAttivo(modificato.isAttivo());

        return tipoVisitaRepo.save(originale);
    }

    /**
     * Restituisce tutti i tipi visita.
     *
     * @return elenco dei tipi visita
     */
    public List<TipoVisita> ottieniTutti() {
        return tipoVisitaRepo.findByIsDeletedFalse();
    }

    /**
     * Restituisce un tipo visita dato l'id.
     *
     * @param id identificativo del tipo visita
     * @return tipo visita trovato
     */
    public TipoVisita ottieniPerId(Integer id) {
        return tipoVisitaRepo.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tipo visite non trovato."));
    }

    @Transactional
    /**
     * Elimina un tipo visita esistente.
     *
     * @param id identificativo del tipo visita da eliminare
     */
    public void eliminaTipoVisite(Integer id) {
        TipoVisita tipoVisita = tipoVisitaRepo.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Impossibile eliminare: Tipo visite non trovato."));
        tipoVisita.setIsDeleted(true);
        tipoVisitaRepo.save(tipoVisita);
    }
}
