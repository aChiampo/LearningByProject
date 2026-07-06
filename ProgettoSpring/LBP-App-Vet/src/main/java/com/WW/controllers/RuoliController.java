package com.WW.controllers;

import com.WW.entities.Ruoli;
import com.WW.services.RuoliService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ruoli")
@CrossOrigin(origins = "*")
public class RuoliController {

    private final RuoliService ruoliService;

    public RuoliController(RuoliService ruoliService) {
        this.ruoliService = ruoliService;
    }

    /**
     * Endpoint GET per visualizzare tutti i ruoli
     * @return lista di tutti i ruoli
     */
    @GetMapping
    public ResponseEntity<List<Ruoli>> getAllRuoli() {
        try {
            List<Ruoli> ruoli = ruoliService.visualizzaTuttiRuoli();
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
    public ResponseEntity<Ruoli> getRuoloById(@PathVariable Integer id) {
        try {
            Optional<Ruoli> ruolo = ruoliService.getRuoloById(id);
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
    public ResponseEntity<List<Ruoli>> findRuoliById(@RequestParam Integer id) {
        try {
            List<Ruoli> ruoli = ruoliService.findRuoliById(id);
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
    public ResponseEntity<List<Ruoli>> findByRuolo(@RequestParam String ruolo) {
        try {
            List<Ruoli> ruoli = ruoliService.findByRuolo(ruolo);
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
    public ResponseEntity<Ruoli> createRuolo(@RequestBody Ruoli ruolo) {
        try {
            Ruoli ruoloCreato = ruoliService.salvaRuolo(ruolo);
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
    public ResponseEntity<Ruoli> updateRuolo(
            @PathVariable Integer id,
            @RequestBody Ruoli ruolo) {
        try {
            ruolo.setId(id);
            Ruoli ruoloAggiornato = ruoliService.aggiornaRuolo(ruolo);
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
            ruoliService.eliminaRuolo(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
