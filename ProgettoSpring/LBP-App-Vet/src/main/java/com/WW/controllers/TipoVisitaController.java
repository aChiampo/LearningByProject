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

import com.WW.dto.TipoVisitaRequest;
import com.WW.entities.CategoriaVisite;
import com.WW.entities.TipoVisita;
import com.WW.entities.Utente;
import com.WW.services.TipoVisitaService;

import lombok.RequiredArgsConstructor;

/**
 * REST controller per la gestione dei tipi visita.
 */
@RestController
@RequestMapping("/api/tipiVisite")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TipoVisitaController {

    private final TipoVisitaService tipoVisitaService;

    /**
     * Restituisce tutti i tipi visita.
     *
     * @return elenco dei tipi visita
     */
    @GetMapping("/ottieniTutti")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<TipoVisita>> ottieniTutti() {
        return ResponseEntity.ok(tipoVisitaService.ottieniTutti());
    }

    /**
     * Restituisce un tipo visita a partire dall'id.
     *
     * @param id identificativo del tipo visita
     * @return tipo visita trovato
     */
    @GetMapping("/ottieni/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<TipoVisita> ottieni(@PathVariable Integer id) {
        return ResponseEntity.ok(tipoVisitaService.ottieniPerId(id));
    }

    /**
     * Crea un nuovo tipo visita.
     *
     * @param request dati del tipo visita da salvare
     * @return tipo visita creato
     */
    @PostMapping("/aggiungi")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<TipoVisita> crea(@RequestBody TipoVisitaRequest request) {
        TipoVisita nuovoTipoVisite = tipoVisitaService.aggiungiTipoVisite(toEntity(request));
        return new ResponseEntity<>(nuovoTipoVisite, HttpStatus.CREATED);
    }

    /**
     * Aggiorna un tipo visita esistente.
     *
     * @param id identificativo del tipo visita da aggiornare
     * @param request nuovi dati da applicare
     * @return tipo visita aggiornato
     */
    @PatchMapping("/modifica/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<TipoVisita> modifica(@PathVariable Integer id, @RequestBody TipoVisitaRequest request) {
        TipoVisita tipoVisiteAggiornato = tipoVisitaService.modificaTipoVisite(id, toEntity(request));
        return ResponseEntity.ok(tipoVisiteAggiornato);
    }

    private TipoVisita toEntity(TipoVisitaRequest request) {
        TipoVisita tipoVisita = new TipoVisita();
        tipoVisita.setNome(request.nome());
        if (request.durata() != null) {
            tipoVisita.setDurata(request.durata());
        }
        tipoVisita.setPrezzo(request.prezzo());
        tipoVisita.setIsDeleted(false);
        tipoVisita.setAttivo(request.attivo() == null || request.attivo());

        if (request.idCategoria() != null) {
            CategoriaVisite categoria = new CategoriaVisite();
            categoria.setId(request.idCategoria());
            tipoVisita.setCategoria(categoria);
        }

        if (request.idDottore() != null) {
            Utente dottore = new Utente();
            dottore.setId(request.idDottore());
            tipoVisita.setDottore(dottore);
        }

        return tipoVisita;
    }

    /**
     * Elimina un tipo visita esistente con soft delete.
     *
     * @param id identificativo del tipo visita da eliminare
     * @return risposta senza contenuto
     */
    @PatchMapping("/elimina/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<Void> elimina(@PathVariable Integer id) {
        tipoVisitaService.eliminaTipoVisite(id);
        return ResponseEntity.noContent().build();
    }
}
