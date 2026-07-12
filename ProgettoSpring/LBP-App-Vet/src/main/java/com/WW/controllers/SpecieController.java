package com.WW.controllers;

import com.WW.entities.Specie;
import com.WW.services.SpecieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/specie")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class SpecieController {

    private final SpecieService specieService;

    public SpecieController(SpecieService specieService) {
        this.specieService = specieService;
    }

    /**
     * Endpoint GET per visualizzare tutte le specie
     * @return lista di tutte le specie
     */
    @GetMapping
    public ResponseEntity<List<Specie>> getAllSpecie() {
        try {
            List<Specie> specie = specieService.visualizzaTutteSpecie();
            return ResponseEntity.ok(specie);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per recuperare una specie per ID
     * @param id l'ID della specie
     * @return la specie se trovata
     */
    @GetMapping("/{id}")
    public ResponseEntity<Specie> getSpecieById(@PathVariable Integer id) {
        try {
            Optional<Specie> specie = specieService.getSpecieById(id);
            return specie.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per ricercare specie per nome
     * @param nome il nome della specie
     * @return lista di specie corrispondenti
     */
    @GetMapping("/cerca/nome")
    public ResponseEntity<List<Specie>> findByNome(@RequestParam String nome) {
        try {
            List<Specie> specie = specieService.findByNome(nome);
            return ResponseEntity.ok(specie);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint POST per creare una nuova specie
     * @param specie i dati della specie da creare
     * @return la specie creata
     */
    @PostMapping
    public ResponseEntity<Specie> createSpecie(@RequestBody Specie specie) {
        try {
            Specie specieCreata = specieService.salvaSpecie(specie);
            return ResponseEntity.status(HttpStatus.CREATED).body(specieCreata);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint PUT per aggiornare una specie esistente
     * @param id l'ID della specie da aggiornare
     * @param specie i nuovi dati della specie
     * @return la specie aggiornata
     */
    @PutMapping("/{id}")
    public ResponseEntity<Specie> updateSpecie(
            @PathVariable Integer id,
            @RequestBody Specie specie) {
        try {
            specie.setId(id);
            Specie specieAggiornata = specieService.aggiornaSpecie(specie);
            return ResponseEntity.ok(specieAggiornata);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint DELETE per eliminare una specie (soft delete)
     * @param id l'ID della specie da eliminare
     * @return status 204 No Content se eliminata con successo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpecie(@PathVariable Integer id) {
        try {
            specieService.eliminaSpecie(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint DELETE per eliminare fisicamente una specie dal database
     * @param id l'ID della specie da eliminare
     * @return status 204 No Content se eliminata con successo
     */
    @DeleteMapping("/{id}/fisico")
    public ResponseEntity<Void> deleteSpecieFisica(@PathVariable Integer id) {
        try {
            specieService.eliminaSpecieFisica(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
