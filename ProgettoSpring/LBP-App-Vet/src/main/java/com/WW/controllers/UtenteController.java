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

import com.WW.entities.Utente;
import com.WW.services.UtenteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/utenti")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UtenteController {

    private final UtenteService utenteService;

    @GetMapping("/ottieniTutti")
    public ResponseEntity<List<Utente>> ottieniTutti() {
        return ResponseEntity.ok(utenteService.ottieniTutti());
    }

    @GetMapping("/ottieni/{id}")
    public ResponseEntity<Utente> ottieni(@PathVariable Integer id) {
        return ResponseEntity.ok(utenteService.ottieniPerId(id));
    }

    @PostMapping("/aggiungi")
    public ResponseEntity<Utente> crea(@RequestBody Utente utente) {
        Utente nuovoUtente = utenteService.aggiungiUtente(utente);
        return new ResponseEntity<>(nuovoUtente, HttpStatus.CREATED);
    }

    @PatchMapping("/modifica/{id}")
    public ResponseEntity<Utente> modifica(@PathVariable Integer id, @RequestBody Utente utente) {
        Utente utenteAggiornato = utenteService.modificaUtente(id, utente);
        return ResponseEntity.ok(utenteAggiornato);
    }

    @DeleteMapping("/elimina/{id}")
    public ResponseEntity<Void> elimina(@PathVariable Integer id) {
        utenteService.eliminaUtente(id);
        return ResponseEntity.noContent().build();
    }
}
