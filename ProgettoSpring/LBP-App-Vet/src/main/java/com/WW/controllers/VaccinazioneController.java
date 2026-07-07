package com.WW.controllers;

import com.WW.entities.Vaccinazione;
import com.WW.entities.TipoVaccino;
import com.WW.services.VaccinazioneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vaccinazioni")
@CrossOrigin(origins = "*")
public class VaccinazioneController {

    private final VaccinazioneService vaccinazioniService;

    public VaccinazioneController(VaccinazioneService vaccinazioniService) {
        this.vaccinazioniService = vaccinazioniService;
    }

    /**
     * Endpoint GET per visualizzare tutte le vaccinazioni
     * @return lista di tutte le vaccinazioni
     */
    @GetMapping
    public ResponseEntity<List<Vaccinazione>> getAllVaccinazioni() {
        try {
            List<Vaccinazione> vaccinazioni = vaccinazioniService.visualizzaTutteVaccinazioni();
            return ResponseEntity.ok(vaccinazioni);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per recuperare una vaccinazione per ID
     * @param id l'ID della vaccinazione
     * @return la vaccinazione se trovata
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vaccinazione> getVaccinazioneById(@PathVariable Integer id) {
        try {
            Optional<Vaccinazione> vaccinazione = vaccinazioniService.getVaccinazioneById(id);
            return vaccinazione.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per ricercare vaccinazioni per tipo di vaccino
     * @param idTipoVaccino l'ID del tipo di vaccino
     * @return lista di vaccinazioni del tipo specificato
     */
    @GetMapping("/cerca/tipo/{idTipoVaccino}")
    public ResponseEntity<List<Vaccinazione>> getVaccinazioniByTipo(@PathVariable Integer idTipoVaccino) {
        try {
            TipoVaccino tipoVaccino = new TipoVaccino();
            tipoVaccino.setId(idTipoVaccino);
            List<Vaccinazione> vaccinazioni = vaccinazioniService.findByIdTipoVaccino(tipoVaccino);
            return ResponseEntity.ok(vaccinazioni);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per ricercare vaccinazioni per data
     * @param data la data di vaccinazione (formato: yyyy-MM-ddTHH:mm:ss)
     * @return lista di vaccinazioni della data specificata
     */
    @GetMapping("/cerca/data")
    public ResponseEntity<List<Vaccinazione>> getVaccinazioniByData(@RequestParam LocalDateTime data) {
        try {
            List<Vaccinazione> vaccinazioni = vaccinazioniService.findByDataVaccinazione(data);
            return ResponseEntity.ok(vaccinazioni);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint GET per ricercare vaccinazioni per lotto
     * @param lotto il numero di lotto
     * @return lista di vaccinazioni del lotto specificato
     */
    @GetMapping("/cerca/lotto")
    public ResponseEntity<List<Vaccinazione>> getVaccinazioniByLotto(@RequestParam String lotto) {
        try {
            List<Vaccinazione> vaccinazioni = vaccinazioniService.findByLotto(lotto);
            return ResponseEntity.ok(vaccinazioni);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint POST per creare una nuova vaccinazione
     * @param vaccinazione i dati della vaccinazione da creare
     * @return la vaccinazione creata
     */
    @PostMapping
    public ResponseEntity<Vaccinazione> createVaccinazione(@RequestBody Vaccinazione vaccinazione) {
        try {
            Vaccinazione vaccinazioneCreata = vaccinazioniService.salvaVaccinazione(vaccinazione);
            return ResponseEntity.status(HttpStatus.CREATED).body(vaccinazioneCreata);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint PUT per aggiornare una vaccinazione esistente
     * @param id l'ID della vaccinazione da aggiornare
     * @param vaccinazione i nuovi dati della vaccinazione
     * @return la vaccinazione aggiornata
     */
    @PutMapping("/{id}")
    public ResponseEntity<Vaccinazione> updateVaccinazione(
            @PathVariable Integer id,
            @RequestBody Vaccinazione vaccinazione) {
        try {
            vaccinazione.setId(id);
            Vaccinazione vaccinazioneAggiornata = vaccinazioniService.aggiornaVaccinazione(vaccinazione);
            return ResponseEntity.ok(vaccinazioneAggiornata);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint DELETE per eliminare una vaccinazione
     * @param id l'ID della vaccinazione da eliminare
     * @return status 204 No Content se eliminata con successo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVaccinazione(@PathVariable Integer id) {
        try {
            vaccinazioniService.eliminaVaccinazione(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
