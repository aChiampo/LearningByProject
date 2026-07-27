package com.WW.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.WW.dto.CategoriaVisiteRequest;
import com.WW.entities.CategoriaVisite;
import com.WW.services.CategoriaVisiteService;

import lombok.RequiredArgsConstructor;

/**
 * REST controller per la gestione delle categorie visite.
 */
@RestController
@RequestMapping("/api/categorieVisite")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CategoriaVisiteController {

    private final CategoriaVisiteService categoriaVisiteService;

    /**
     * Restituisce tutte le categorie visita.
     *
     * @return elenco delle categorie
     */
    @GetMapping("/ottieniTutte")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<CategoriaVisite>> ottieniTutte() {
        return ResponseEntity.ok(categoriaVisiteService.ottieniTutte());
    }

    /**
     * Restituisce una categoria visita a partire dall'id.
     *
     * @param id identificativo della categoria
     * @return categoria trovata
     */
    @GetMapping("/ottieni/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<CategoriaVisite> ottieni(@PathVariable Integer id) {
        return ResponseEntity.ok(categoriaVisiteService.ottieniPerId(id));
    }

    /**
     * Crea una nuova categoria visita.
     *
     * @param request dati della categoria da salvare
     * @return categoria creata
     */
    @PostMapping("/aggiungi")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<CategoriaVisite> aggiungi(@RequestBody CategoriaVisiteRequest request) {
        CategoriaVisite nuovaCategoriaVisite = categoriaVisiteService.aggiungiCategoriaVisite(toEntity(request));
        return new ResponseEntity<>(nuovaCategoriaVisite, HttpStatus.CREATED);
    }

    /**
     * Aggiorna una categoria visita esistente.
     *
     * @param id identificativo della categoria da aggiornare
     * @param request nuovi dati da applicare
     * @return categoria aggiornata
     */
    @PatchMapping("/modifica/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<CategoriaVisite> modifica(
            @PathVariable Integer id,
            @RequestBody CategoriaVisiteRequest request) {
        CategoriaVisite categoriaVisiteAggiornata = categoriaVisiteService.modificaCategoriaVisite(id, toEntity(request));
        return ResponseEntity.ok(categoriaVisiteAggiornata);
    }

    private CategoriaVisite toEntity(CategoriaVisiteRequest request) {
        CategoriaVisite categoriaVisite = new CategoriaVisite();
        categoriaVisite.setNome(request.nome());
        categoriaVisite.setIsDeleted(false);
        return categoriaVisite;
    }

    /**
     * Elimina una categoria visita esistente con soft delete.
     *
     * @param id identificativo della categoria da eliminare
     * @return risposta senza contenuto
     */
    @PatchMapping("/elimina/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<Void> elimina(@PathVariable Integer id) {
        categoriaVisiteService.eliminaCategoriaVisite(id);
        return ResponseEntity.noContent().build();
    }
}
