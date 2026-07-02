package com.WW.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.WW.entities.TipoVisite;
import com.WW.services.TipoVisiteService;

import lombok.RequiredArgsConstructor;

/**
 * REST controller per la gestione dei tipi visita.
 */
@RestController
@RequestMapping("/api/tipiVisite")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TipoVisiteController {

    private final TipoVisiteService tipoVisiteService;

    /**
     * Restituisce tutti i tipi visita.
     *
     * @return elenco dei tipi visita
     */
    @GetMapping("/ottieniTutti")
    public ResponseEntity<List<TipoVisite>> ottieniTutti() {
        return ResponseEntity.ok(tipoVisiteService.ottieniTutti());
    }

    /**
     * Restituisce un tipo visita a partire dall'id.
     *
     * @param id identificativo del tipo visita
     * @return tipo visita trovato
     */
    @GetMapping("/ottieni/{id}")
    public ResponseEntity<TipoVisite> ottieni(@PathVariable Integer id) {
        return ResponseEntity.ok(tipoVisiteService.ottieniPerId(id));
    }

    /**
     * Crea un nuovo tipo visita.
     *
     * @param tipoVisite dati del tipo visita da salvare
     * @return tipo visita creato
     */
    @PostMapping("/aggiungi")
    public ResponseEntity<TipoVisite> crea(@RequestBody TipoVisite tipoVisite) {
        TipoVisite nuovoTipoVisite = tipoVisiteService.aggiungiTipoVisite(tipoVisite);
        return new ResponseEntity<>(nuovoTipoVisite, HttpStatus.CREATED);
    }

    /**
     * Aggiorna un tipo visita esistente.
     *
     * @param id identificativo del tipo visita da aggiornare
     * @param tipoVisite nuovi dati da applicare
     * @return tipo visita aggiornato
     */
    @PatchMapping("/modifica/{id}")
    public ResponseEntity<TipoVisite> modifica(@PathVariable Integer id, @RequestBody TipoVisite tipoVisite) {
        TipoVisite tipoVisiteAggiornato = tipoVisiteService.modificaTipoVisite(id, tipoVisite);
        return ResponseEntity.ok(tipoVisiteAggiornato);
    }

    /**
     * Elimina un tipo visita esistente.
     *
     * @param id identificativo del tipo visita da eliminare
     * @return risposta senza contenuto
     */
    @DeleteMapping("/elimina/{id}")
    public ResponseEntity<Void> elimina(@PathVariable Integer id) {
        tipoVisiteService.eliminaTipoVisite(id);
        return ResponseEntity.noContent().build();
    }
}
