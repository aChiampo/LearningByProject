package com.WW.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.WW.entities.Ruolo;
import com.WW.services.RuoloService;
/**
 * Controller REST per la gestione dei ruoli applicativi.
 */

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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Ruolo> getRuoloById(@PathVariable Integer id) {
        try {
            Ruolo ruolo = ruoloService.ottieniPerId(id);
            return ResponseEntity.ok(ruolo);
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Ruolo> findByRuolo(@RequestParam String ruolo) {
        try {
            Optional<Ruolo> ruoloOptional = ruoloService.ottieniPerRuolo(ruolo);
            return ruoloOptional.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
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
