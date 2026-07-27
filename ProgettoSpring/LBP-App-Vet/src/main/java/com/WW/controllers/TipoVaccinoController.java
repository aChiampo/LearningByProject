package com.WW.controllers;

import com.WW.dto.TipoVaccinoRequest;
import com.WW.entities.TipoVaccino;
import com.WW.services.TipoVaccinoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
/**
 * Controller REST per la gestione dei tipi vaccino.
 */

@RestController
@RequestMapping("/api/tipi-vaccino")
@CrossOrigin(origins = "*")
public class TipoVaccinoController {

    private final TipoVaccinoService tipoVaccinoService;

    public TipoVaccinoController(TipoVaccinoService tipoVaccinoService) {
        this.tipoVaccinoService = tipoVaccinoService;
    }

    /**
     * Endpoint GET per visualizzare tutti i tipi di vaccino
     * @return lista di tutti i tipi di vaccino
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<TipoVaccino>> getAllTipiVaccino() {
        try {
            List<TipoVaccino> tipiVaccino = tipoVaccinoService.visualizzaTuttiTipiVaccino();
            return ResponseEntity.ok(tipiVaccino);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per recuperare un tipo di vaccino per ID
     * @param id l'ID del tipo di vaccino
     * @return il tipo di vaccino se trovato
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<TipoVaccino> getTipoVaccinoById(@PathVariable Integer id) {
        try {
            Optional<TipoVaccino> tipoVaccino = tipoVaccinoService.getTipoVaccinoById(id);
            return tipoVaccino.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per ricercare tipi di vaccino per tipologia
     * @param tipologia la tipologia del vaccino
     * @return lista di tipi di vaccino corrispondenti
     */
    @GetMapping("/cerca/tipologia")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<TipoVaccino>> getTipiVaccinoByTipologia(@RequestParam String tipologia) {
        try {
            List<TipoVaccino> tipiVaccino = tipoVaccinoService.findByTipologia(tipologia);
            return ResponseEntity.ok(tipiVaccino);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per ricercare tipi di vaccino per durata
     * @param durata la durata del vaccino in mesi
     * @return lista di tipi di vaccino con la durata specificata
     */
    @GetMapping("/cerca/durata")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<TipoVaccino>> getTipiVaccinoByDurata(@RequestParam int durata) {
        try {
            List<TipoVaccino> tipiVaccino = tipoVaccinoService.findByDurata(durata);
            return ResponseEntity.ok(tipiVaccino);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint POST per creare un nuovo tipo di vaccino
     * @param request i dati del tipo di vaccino da creare
     * @return il tipo di vaccino creato
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<TipoVaccino> createTipoVaccino(@RequestBody TipoVaccinoRequest request) {
        try {
            TipoVaccino tipoVaccinoCreato = tipoVaccinoService.salvaTipoVaccino(toEntity(request));
            return ResponseEntity.status(HttpStatus.CREATED).body(tipoVaccinoCreato);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint PUT per aggiornare un tipo di vaccino esistente
     * @param id l'ID del tipo di vaccino da aggiornare
     * @param request i nuovi dati del tipo di vaccino
     * @return il tipo di vaccino aggiornato
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<TipoVaccino> updateTipoVaccino(
            @PathVariable Integer id,
            @RequestBody TipoVaccinoRequest request) {
        try {
            TipoVaccino tipoVaccino = toEntity(request);
            tipoVaccino.setId(id);
            TipoVaccino tipoVaccinoAggiornato = tipoVaccinoService.aggiornaTipoVaccino(tipoVaccino);
            return ResponseEntity.ok(tipoVaccinoAggiornato);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private TipoVaccino toEntity(TipoVaccinoRequest request) {
        TipoVaccino tipoVaccino = new TipoVaccino();
        tipoVaccino.setTipologia(request.tipologia());
        if (request.durata() != null) {
            tipoVaccino.setDurata(request.durata());
        }
        tipoVaccino.setNote(request.note());
        tipoVaccino.setIsDeleted(false);
        return tipoVaccino;
    }

    /**
     * Endpoint PATCH per eliminare un tipo di vaccino (soft delete)
     * @param id l'ID del tipo di vaccino da eliminare
     * @return status 204 No Content se eliminato con successo
     */
    @PatchMapping("/{id}/elimina")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<Void> deleteTipoVaccino(@PathVariable Integer id) {
        try {
            tipoVaccinoService.eliminaTipoVaccino(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
