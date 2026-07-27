package com.WW.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.WW.entities.CategoriaVisite;
import com.WW.repositories.CategoriaVisiteRepo;

import lombok.RequiredArgsConstructor;

/**
 * @author: A. Chiampo
 * Servizi disponibili per la tabella CATEGORIE_VISITE
 * Last update: 26/06/2026
 */
@Service
@RequiredArgsConstructor
public class CategoriaVisiteService {

    private final CategoriaVisiteRepo categoriaVisiteRepo;

    /**
     * Salva una nuova categoria visita.
     *
     * @param categoriaVisite dati della categoria da salvare
     * @return categoria creata
     */
    @Transactional
    public CategoriaVisite aggiungiCategoriaVisite(CategoriaVisite categoriaVisite) {
        if (categoriaVisite.getIsDeleted() == null) {
            categoriaVisite.setIsDeleted(false);
        }
        return categoriaVisiteRepo.save(categoriaVisite);
    }

    /**
     * Aggiorna una categoria visita esistente.
     *
     * @param id identificativo della categoria da aggiornare
     * @param modificato nuovi dati da applicare
     * @return categoria aggiornata
     */
    @Transactional
    public CategoriaVisite modificaCategoriaVisite(Integer id, CategoriaVisite modificato) {
        CategoriaVisite originale = ottieniPerId(id);

        if (modificato.getNome() != null && !modificato.getNome().isBlank()) {
            originale.setNome(modificato.getNome());
        }

        return categoriaVisiteRepo.save(originale);
    }

    /**
     * Restituisce tutte le categorie visita.
     *
     * @return elenco delle categorie
     */
    public List<CategoriaVisite> ottieniTutte() {
        return categoriaVisiteRepo.findByIsDeletedFalse();
    }
    /**
     * Restituisce una categoria visita non eliminata dato il suo id.
     *
     * @param id identificativo della categoria
     * @return categoria trovata
     */
    public CategoriaVisite ottieniPerId(Integer id) {
        return categoriaVisiteRepo.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoria visite non trovata."));
    }

    /**
     * Elimina una categoria visita esistente.
     *
     * @param id identificativo della categoria da eliminare
     */
    @Transactional
    public void eliminaCategoriaVisite(Integer id) {
        CategoriaVisite categoriaVisite = categoriaVisiteRepo.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Impossibile eliminare: Categoria visite non trovata."));
        categoriaVisite.setIsDeleted(true);
        categoriaVisiteRepo.save(categoriaVisite);
    }
}
