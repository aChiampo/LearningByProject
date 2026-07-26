package com.WW.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.WW.dto.RazzaRequest;
import com.WW.entities.Razza;
import com.WW.entities.Specie;
import com.WW.services.RazzaService;

@RestController
@RequestMapping("/api/razze")
@CrossOrigin(origins = "*")
public class RazzaController {

    private final RazzaService razzaService;

    public RazzaController(RazzaService razzaService) {
        this.razzaService = razzaService;
    }

    private Razza toRazza(RazzaRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("I dati della razza sono obbligatori");
        }

        Specie specie = new Specie();
        specie.setId(request.idSpecie());
        specie.setDeleted(false);

        Razza razza = new Razza();
        razza.setNome(request.nome());
        razza.setIdSpecie(specie);
        razza.setDeleted(false);

        return razza;
    }

    /**
     * Endpoint GET per visualizzare tutte le razze
     * @return lista di tutte le razze
     */
    @GetMapping("")
    public ResponseEntity<List<Razza>> getAllRazze() {
        try {
            List<Razza> razze = razzaService.visualizzaTutteRazze();
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
    public ResponseEntity<Razza> getRazzaById(@PathVariable Integer id) {
        try {
            Optional<Razza> razza = razzaService.getRazzaById(id);
            return razza.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per ricercare razze per ID della specie
     * @param specieId l'ID della specie
     * @return lista di razze corrispondenti alla specie
     */
    @GetMapping("/specie/{specieId}")
    public ResponseEntity<List<Razza>> findByIdSpecie(@PathVariable Integer specieId) {
        try {
            List<Razza> razze = razzaService.findByIdSpecie_Id(specieId);
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
    public ResponseEntity<List<Razza>> findByNome(@RequestParam String nome) {
        try {
            List<Razza> razze = razzaService.findByNome(nome);
            return ResponseEntity.ok(razze);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint POST per creare una nuova razza
     * @param request i dati della razza da creare
     * @return la razza creata
     */
    @PostMapping("/aggiungiRazza")
    public ResponseEntity<Razza> createRazza(@RequestBody RazzaRequest request) {
        try {
            Razza razza = toRazza(request);
            Razza razzaCreata = razzaService.salvaRazza(razza);
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
     * @param request i nuovi dati della razza
     * @return la razza aggiornata
     */
    @PutMapping("/{id}")
    public ResponseEntity<Razza> updateRazza(
            @PathVariable Integer id,
            @RequestBody RazzaRequest request) {
        try {
            Razza razza = toRazza(request);
             razza.setId(id);
            Razza razzaAggiornata = razzaService.aggiornaRazza(razza);
            return ResponseEntity.ok(razzaAggiornata);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint PATCH per eliminare una razza (soft delete)
     * @param id l'ID della razza da eliminare
     * @return status 204 No Content se eliminata con successo
     */
    @PatchMapping("/{id}/elimina")
    public ResponseEntity<Void> deleteRazza(@PathVariable Integer id) {
        try {
            razzaService.eliminaRazza(id);
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
            razzaService.eliminaRazzaFisica(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
