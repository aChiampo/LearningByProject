package com.WW.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.WW.entities.Visita;
import com.WW.services.PrenotazioniService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/prenotazioni")
@RequiredArgsConstructor
public class PrenotazioniController {

    private final PrenotazioniService prenotazioniService;

    @GetMapping("/leggiTutti")
    public ResponseEntity<List<Visita>> leggiTutti() {
        return ResponseEntity.ok(prenotazioniService.ottieniTutte());
    }

    @GetMapping("/leggi/{id}")
    public ResponseEntity<Visita> leggi(@PathVariable Integer id) {
        return ResponseEntity.ok(prenotazioniService.ottieniPerId(id));
    }

    @GetMapping("/leggiPerAnimale/{animaleId}")
    public ResponseEntity<List<Visita>> leggiPerAnimale(@PathVariable Integer animaleId) {
        return ResponseEntity.ok(prenotazioniService.ottieniPerAnimale(animaleId));
    }

    @GetMapping("/leggiPerVeterinario/{veterinarioId}")
    public ResponseEntity<List<Visita>> leggiPerVeterinario(@PathVariable Integer veterinarioId) {
        return ResponseEntity.ok(prenotazioniService.ottieniPerVeterinario(veterinarioId));
    }

    @PostMapping("/crea")
    public ResponseEntity<Visita> crea(@RequestBody Visita visita) {
        Visita nuovaPrenotazione = prenotazioniService.creaPrenotazione(visita);
        return new ResponseEntity<>(nuovaPrenotazione, HttpStatus.CREATED);
    }

    @PatchMapping("/modifica/{id}")
    public ResponseEntity<Visita> modifica(@PathVariable Integer id, @RequestBody Visita visita) {
        Visita prenotazioneAggiornata = prenotazioniService.aggiornaPrenotazione(id, visita);
        return ResponseEntity.ok(prenotazioneAggiornata);
    }

    @DeleteMapping("/elimina/{id}")
    public ResponseEntity<Void> elimina(@PathVariable Integer id) {
        prenotazioniService.eliminaPrenotazione(id);
        return ResponseEntity.noContent().build();
    }
}
