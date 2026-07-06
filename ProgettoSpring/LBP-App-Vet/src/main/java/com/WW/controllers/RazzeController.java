package com.WW.controllers;

import com.WW.entities.Razze;
import com.WW.services.RazzeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/razze")
@CrossOrigin(origins = "*")
public class RazzeController {

    private final RazzeService razzeService;

    public RazzeController(RazzeService razzeService) {
        this.razzeService = razzeService;
    }

    /**
     * Endpoint GET per visualizzare tutte le razze
     * @return lista di tutte le razze
     */
    @GetMapping
    public ResponseEntity<List<Razze>> getAllRazze() {
        try {
            List<Razze> razze = razzeService.visualizzaTutteRazze();
            return ResponseEntity.ok(razze);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per recuperare una razza per ID
     * @param id l'ID della razza
     * @return la razza se trovata
     */
    @GetMapping("/{id}")
    public ResponseEntity<Razze> getRazzaById(@PathVariable Integer id) {
        try {
            Optional<Razze> razza = razzeService.getRazzaById(id);
            return razza.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per ricercare razze per ID
     * @param id l'ID della razza
     * @return lista di razze corrispondenti
     */
    @GetMapping("/cerca/id")
    public ResponseEntity<List<Razze>> findRazzeById(@RequestParam Integer id) {
        try {
            List<Razze> razze = razzeService.findRazzeById(id);
            return ResponseEntity.ok(razze);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per ricercare razze per nome
     * @param nome il nome della razza
     * @return lista di razze corrispondenti
     */
    @GetMapping("/cerca/nome")
    public ResponseEntity<List<Razze>> findByNome(@RequestParam String nome) {
        try {
            List<Razze> razze = razzeService.findByNome(nome);
            return ResponseEntity.ok(razze);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint POST per creare una nuova razza
     * @param razza i dati della razza da creare
     * @return la razza creata
     */
    @PostMapping
    public ResponseEntity<Razze> createRazza(@RequestBody Razze razza) {
        try {
            Razze razzaCreata = razzeService.salvaRazza(razza);
            return ResponseEntity.status(HttpStatus.CREATED).body(razzaCreata);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint PUT per aggiornare una razza esistente
     * @param id l'ID della razza da aggiornare
     * @param razza i nuovi dati della razza
     * @return la razza aggiornata
     */
    @PutMapping("/{id}")
    public ResponseEntity<Razze> updateRazza(
            @PathVariable Integer id,
            @RequestBody Razze razza) {
        try {
            razza.setId(id);
            Razze razzaAggiornata = razzeService.aggiornaRazza(razza);
            return ResponseEntity.ok(razzaAggiornata);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint DELETE per eliminare una razza (soft delete)
     * @param id l'ID della razza da eliminare
     * @return status 204 No Content se eliminata con successo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRazza(@PathVariable Integer id) {
        try {
            razzeService.eliminaRazza(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint DELETE per eliminare fisicamente una razza dal database
     * @param id l'ID della razza da eliminare
     * @return status 204 No Content se eliminata con successo
     */
    @DeleteMapping("/{id}/fisico")
    public ResponseEntity<Void> deleteRazzaFisica(@PathVariable Integer id) {
        try {
            razzeService.eliminaRazzaFisica(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
