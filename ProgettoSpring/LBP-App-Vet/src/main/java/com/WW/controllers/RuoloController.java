package com.WW.controllers;

import com.WW.entities.Ruolo;
import com.WW.services.RuoloService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ruoli")
@CrossOrigin(origins = "*")
public class RuoloController {

    private final RuoloService ruoloService;

    public RuoloController(RuoloService ruoloService) {
        this.ruoloService = ruoloService;
    }

    /**
     * Endpoint GET per visualizzare tutti i ruoli
     * @return lista di tutti i ruoli
     */
    @GetMapping
    public ResponseEntity<List<Ruolo>> getAllRuoli() {
        try {
            List<Ruolo> ruoli = ruoloService.visualizzaTuttiRuoli();
            return ResponseEntity.ok(ruoli);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per recuperare un ruolo per ID
     * @param id l'ID del ruolo
     * @return il ruolo se trovato
     */
    @GetMapping("/{id}")
    public ResponseEntity<Ruolo> getRuoloById(@PathVariable Integer id) {
        try {
            Optional<Ruolo> ruolo = ruoloService.getRuoloById(id);
            return ruolo.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per ricercare ruoli per ID
     * @param id l'ID del ruolo
     * @return lista di ruoli corrispondenti
     */
    @GetMapping("/cerca/id")
    public ResponseEntity<List<Ruolo>> findRuoliById(@RequestParam Integer id) {
        try {
            List<Ruolo> ruoli = ruoloService.findRuoliById(id);
            return ResponseEntity.ok(ruoli);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per ricercare ruoli per nome
     * @param ruolo il nome del ruolo
     * @return lista di ruoli corrispondenti
     */
    @GetMapping("/cerca/nome")
    public ResponseEntity<List<Ruolo>> findByRuolo(@RequestParam String ruolo) {
        try {
            List<Ruolo> ruoli = ruoloService.findByRuolo(ruolo);
            return ResponseEntity.ok(ruoli);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint POST per creare un nuovo ruolo
     * @param ruolo i dati del ruolo da creare
     * @return il ruolo creato
     */
    @PostMapping
    public ResponseEntity<Ruolo> createRuolo(@RequestBody Ruolo ruolo) {
        try {
            Ruolo ruoloCreato = ruoloService.salvaRuolo(ruolo);
            return ResponseEntity.status(HttpStatus.CREATED).body(ruoloCreato);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint PUT per aggiornare un ruolo esistente
     * @param id l'ID del ruolo da aggiornare
     * @param ruolo i nuovi dati del ruolo
     * @return il ruolo aggiornato
     */
    @PutMapping("/{id}")
    public ResponseEntity<Ruolo> updateRuolo(
            @PathVariable Integer id,
            @RequestBody Ruolo ruolo) {
        try {
            ruolo.setId(id);
            Ruolo ruoloAggiornato = ruoloService.aggiornaRuolo(ruolo);
            return ResponseEntity.ok(ruoloAggiornato);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint DELETE per eliminare un ruolo
     * @param id l'ID del ruolo da eliminare
     * @return status 204 No Content se eliminato con successo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRuolo(@PathVariable Integer id) {
        try {
            ruoloService.eliminaRuolo(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
