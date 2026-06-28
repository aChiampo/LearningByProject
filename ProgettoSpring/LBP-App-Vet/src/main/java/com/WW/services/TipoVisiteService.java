package com.WW.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.WW.entities.TipoVisite;
import com.WW.repositories.TipoVisiteRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVisiteService {

    private final TipoVisiteRepo tipoVisiteRepo;

    public TipoVisite aggiungiTipoVisite(TipoVisite tipoVisite) {
        return tipoVisiteRepo.save(tipoVisite);
    }

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

    public List<TipoVisite> ottieniTutti() {
        return tipoVisiteRepo.findAll();
    }

    public TipoVisite ottieniPerId(Integer id) {
        return tipoVisiteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tipo visite non trovato."));
    }

    public void eliminaTipoVisite(Integer id) {
        if (!tipoVisiteRepo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Impossibile eliminare: Tipo visite non trovato.");
        }
        tipoVisiteRepo.deleteById(id);
    }
}
