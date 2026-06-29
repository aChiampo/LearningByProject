package com.WW.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.WW.entities.CategoriaVisite;
import com.WW.repositories.CategoriaVisiteRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoriaVisiteService {

    private final CategoriaVisiteRepo categoriaVisiteRepo;

    
    public CategoriaVisite aggiungiCategoriaVisite(CategoriaVisite categoriaVisite) {
        return categoriaVisiteRepo.save(categoriaVisite);
    }

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

    public void eliminaCategoriaVisite(Integer id) {
        if (!categoriaVisiteRepo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Impossibile eliminare: Categoria visite non trovata.");
        }
        categoriaVisiteRepo.deleteById(id);
    }
}
