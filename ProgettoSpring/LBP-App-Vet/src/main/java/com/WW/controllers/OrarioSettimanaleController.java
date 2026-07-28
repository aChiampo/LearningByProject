package com.WW.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.WW.entities.OrarioSettimanale;
import com.WW.services.OrarioSettimanaleService;

import lombok.RequiredArgsConstructor;
/**
 * Controller REST per la gestione degli orari settimanali dei veterinari.
 */

@RestController
@RequestMapping("/api/orariSettimanali")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OrarioSettimanaleController {

    private final OrarioSettimanaleService orarioSettimanaleService;

    /**
     * Restituisce tutti gli orari settimanali presenti.
     *
     * @return lista degli orari settimanali
     */
    @GetMapping("/ottieniTutti")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST')")
    public ResponseEntity<List<OrarioSettimanale>> ottieniTutti() {
        return ResponseEntity.ok(orarioSettimanaleService.ottieniTutti());
    }

    /**
     * Restituisce un orario settimanale a partire dall'id.
     *
     * @param id identificativo dell'orario
     * @return orario settimanale trovato
     */
    @GetMapping("/ottieni/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST')")
    public ResponseEntity<OrarioSettimanale> ottieni(@PathVariable Integer id) {
        return ResponseEntity.ok(orarioSettimanaleService.ottieniPerId(id));
    }

    /**
     * Crea un nuovo orario settimanale.
     *
     * @param orarioSettimanale dati dell'orario da salvare
     * @return orario creato
     */
    @PostMapping("/aggiungi")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrarioSettimanale> aggiungi(@RequestBody OrarioSettimanale orarioSettimanale) {
        OrarioSettimanale nuovoOrarioSettimanale = orarioSettimanaleService.aggiungiOrarioSettimanale(orarioSettimanale);
        return new ResponseEntity<>(nuovoOrarioSettimanale, HttpStatus.CREATED);
    }

    /**
     * Aggiorna un orario settimanale esistente.
     *
     * @param id identificativo dell'orario da aggiornare
     * @param orarioSettimanale nuovi dati da applicare
     * @return orario aggiornato
     */
    @PatchMapping("/modifica/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrarioSettimanale> modifica(
            @PathVariable Integer id,
            @RequestBody OrarioSettimanale orarioSettimanale) {
        return ResponseEntity.ok(orarioSettimanaleService.modificaOrarioSettimanale(id, orarioSettimanale));
    }

    /**
     * Elimina un orario settimanale esistente.
     *
     * @param id identificativo dell'orario da eliminare
     * @return risposta senza contenuto
     */
    @DeleteMapping("/elimina/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> elimina(@PathVariable Integer id) {
        orarioSettimanaleService.eliminaOrarioSettimanale(id);
        return ResponseEntity.noContent().build();
    }
}
