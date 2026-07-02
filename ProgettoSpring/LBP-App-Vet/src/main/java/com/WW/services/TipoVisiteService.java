package com.WW.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.WW.entities.TipoVisite;
import com.WW.repositories.TipoVisiteRepo;

import lombok.RequiredArgsConstructor;

/**
 * Servizio per la gestione dei tipi visita.
 */
@Service
@RequiredArgsConstructor
public class TipoVisiteService {

    private final TipoVisiteRepo tipoVisiteRepo;

    /**
     * Salva un nuovo tipo visita.
     *
     * @param tipoVisite dati del tipo visita da salvare
     * @return tipo visita creato
     */
    public TipoVisite aggiungiTipoVisite(TipoVisite tipoVisite) {
        return tipoVisiteRepo.save(tipoVisite);
    }

    /**
     * Aggiorna un tipo visita esistente.
     *
     * @param id identificativo del tipo visita da aggiornare
     * @param modificato nuovi dati da applicare
     * @return tipo visita aggiornato
     */
    public TipoVisite modificaTipoVisite(Integer id, TipoVisite modificato) {
        TipoVisite originale = ottieniPerId(id);

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

        return tipoVisiteRepo.save(originale);
    }

    /**
     * Restituisce tutti i tipi visita.
     *
     * @return elenco dei tipi visita
     */
    public List<TipoVisite> ottieniTutti() {
        return tipoVisiteRepo.findAll();
    }

    /**
     * Restituisce un tipo visita dato l'id.
     *
     * @param id identificativo del tipo visita
     * @return tipo visita trovato
     */
    public TipoVisite ottieniPerId(Integer id) {
        return tipoVisiteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tipo visite non trovato."));
    }

    /**
     * Elimina un tipo visita esistente.
     *
     * @param id identificativo del tipo visita da eliminare
     */
    public void eliminaTipoVisite(Integer id) {
        if (!tipoVisiteRepo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Impossibile eliminare: Tipo visite non trovato.");
        }
        tipoVisiteRepo.deleteById(id);
    }
}
