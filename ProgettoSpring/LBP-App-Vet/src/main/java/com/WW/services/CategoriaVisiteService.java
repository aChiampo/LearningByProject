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

    @Transactional
    public CategoriaVisite aggiungiCategoriaVisite(CategoriaVisite categoriaVisite) {
        return categoriaVisiteRepo.save(categoriaVisite);
    }

    @Transactional
    public CategoriaVisite modificaCategoriaVisite(Integer id, CategoriaVisite modificato) {
        CategoriaVisite originale = ottieniPerId(id);

        if (modificato.getNome() != null && !modificato.getNome().isBlank()) {
            originale.setNome(modificato.getNome());
        }

        return categoriaVisiteRepo.save(originale);
    }

    public List<CategoriaVisite> ottieniTutte() {
        return categoriaVisiteRepo.findAll();
    }


    public CategoriaVisite ottieniPerId(Integer id) {
        return categoriaVisiteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoria visite non trovata."));
    }

    @Transactional
    public void eliminaCategoriaVisite(Integer id) {
        if (!categoriaVisiteRepo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Impossibile eliminare: Categoria visite non trovata.");
        }
        categoriaVisiteRepo.deleteById(id);
    }
}
