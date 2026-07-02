package com.WW.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.WW.entities.CategoriaVisite;
import com.WW.repositories.CategoriaVisiteRepo;

import lombok.RequiredArgsConstructor;

/**
 * Servizio per la gestione delle categorie visite.
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
    public CategoriaVisite aggiungiCategoriaVisite(CategoriaVisite categoriaVisite) {
        return categoriaVisiteRepo.save(categoriaVisite);
    }

    /**
     * Aggiorna una categoria visita esistente.
     *
     * @param id identificativo della categoria da aggiornare
     * @param modificato nuovi dati da applicare
     * @return categoria aggiornata
     */
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
        return categoriaVisiteRepo.findAll();
    }

    /**
     * Restituisce una categoria visita dato l'id.
     *
     * @param id identificativo della categoria
     * @return categoria trovata
     */
    public CategoriaVisite ottieniPerId(Integer id) {
        return categoriaVisiteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Categoria visite non trovata."));
    }

    /**
     * Elimina una categoria visita esistente.
     *
     * @param id identificativo della categoria da eliminare
     */
    public void eliminaCategoriaVisite(Integer id) {
        if (!categoriaVisiteRepo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Impossibile eliminare: Categoria visite non trovata.");
        }
        categoriaVisiteRepo.deleteById(id);
    }
}
