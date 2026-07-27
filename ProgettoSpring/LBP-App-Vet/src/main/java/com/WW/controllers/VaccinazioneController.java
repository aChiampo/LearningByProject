package com.WW.controllers;

import com.WW.dto.VaccinazioneDto;
import com.WW.dto.VaccinazioneRequest;
import com.WW.entities.TipoVaccino;
import com.WW.entities.Vaccinazione;
import com.WW.services.VaccinazioneService;
import jakarta.validation.Valid;
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

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vaccinazioni")
@CrossOrigin(origins = "*")
public class VaccinazioneController {

    private final VaccinazioneService vaccinazioniService;

    public VaccinazioneController(VaccinazioneService vaccinazioniService) {
        this.vaccinazioniService = vaccinazioniService;
    }

    /**
     * Endpoint GET per visualizzare tutte le vaccinazioni.
     * 
     * @return lista DTO di tutte le vaccinazioni
     */
    @GetMapping
    public ResponseEntity<List<VaccinazioneDto>> getAllVaccinazioni() {
        return ResponseEntity.ok(vaccinazioniService.visualizzaTutteVaccinazioniDto());
    }

    /**
     * Endpoint GET per recuperare una vaccinazione per ID.
     * 
     * @param id l'ID della vaccinazione
     * @return la vaccinazione se trovata
     */
    @GetMapping("/{id}")
    public ResponseEntity<VaccinazioneDto> getVaccinazioneById(@PathVariable Integer id) {
        return vaccinazioniService.getVaccinazioneDtoById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint GET per recuperare le vaccinazioni di un animale.
     * 
     * @param idAnimale identificativo dell'animale
     * @return lista DTO delle vaccinazioni dell'animale
     */
    @GetMapping("/animale/{idAnimale}")
    public ResponseEntity<List<VaccinazioneDto>> getVaccinazioniByAnimale(@PathVariable Integer idAnimale) {
        return ResponseEntity.ok(vaccinazioniService.findByIdAnimale(idAnimale));
    }

    /**
     * Endpoint GET per ricercare vaccinazioni per tipo di vaccino.
     * 
     * @param idTipoVaccino l'ID del tipo di vaccino
     * @return lista di vaccinazioni del tipo specificato
     */
    @GetMapping("/cerca/tipo/{idTipoVaccino}")
    public ResponseEntity<List<Vaccinazione>> getVaccinazioniByTipo(@PathVariable Integer idTipoVaccino) {
        TipoVaccino tipoVaccino = new TipoVaccino();
        tipoVaccino.setId(idTipoVaccino);
        return ResponseEntity.ok(vaccinazioniService.findByIdTipoVaccino(tipoVaccino));
    }

    /**
     * Endpoint GET per ricercare vaccinazioni per data.
     * 
     * @param data la data di vaccinazione (formato: yyyy-MM-ddTHH:mm:ss)
     * @return lista di vaccinazioni della data specificata
     */
    @GetMapping("/cerca/data")
    public ResponseEntity<List<Vaccinazione>> getVaccinazioniByData(@RequestParam LocalDateTime data) {
        return ResponseEntity.ok(vaccinazioniService.findByDataVaccinazione(data));
    }

    /**
     * Endpoint GET per ricercare vaccinazioni per lotto.
     * 
     * @param lotto il numero di lotto
     * @return lista di vaccinazioni del lotto specificato
     */
    @GetMapping("/cerca/lotto")
    public ResponseEntity<List<Vaccinazione>> getVaccinazioniByLotto(@RequestParam String lotto) {
        return ResponseEntity.ok(vaccinazioniService.findByLotto(lotto));
    }

    /**
     * Endpoint POST per creare una nuova vaccinazione.
     * 
     * @param request i dati della vaccinazione da creare
     * @return la vaccinazione creata
     */
    @PostMapping
    public ResponseEntity<VaccinazioneDto> createVaccinazione(@Valid @RequestBody VaccinazioneRequest request) {
        VaccinazioneDto vaccinazioneCreata = vaccinazioniService.salvaVaccinazione(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vaccinazioneCreata);
    }

    /**
     * Endpoint PUT per aggiornare una vaccinazione esistente.
     * 
     * @param id      l'ID della vaccinazione da aggiornare
     * @param request i nuovi dati della vaccinazione
     * @return la vaccinazione aggiornata
     */
    @PutMapping("/{id}")
    public ResponseEntity<VaccinazioneDto> updateVaccinazione(
            @PathVariable Integer id,
            @Valid @RequestBody VaccinazioneRequest request) {
        return ResponseEntity.ok(vaccinazioniService.aggiornaVaccinazione(id, request));
    }

    /**
     * Endpoint DELETE per eliminare una vaccinazione.
     * 
     * @param id l'ID della vaccinazione da eliminare
     * @return status 204 No Content se eliminata con successo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVaccinazione(@PathVariable Integer id) {
        vaccinazioniService.eliminaVaccinazione(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint GET per ottenere i vaccini in scadenza nei prossimi N giorni
     * 
     * @param giorni finestra temporale in giorni (default: 30)
     * @return lista delle vaccinazioni in scadenza
     */
    @GetMapping("/in-scadenza")
    public ResponseEntity<?> getVaccinazioniInScadenza(@RequestParam(defaultValue = "30") int giorni) {
        try {
            List<Vaccinazione> vaccinazioni = vaccinazioniService.findVaccinazioniInScadenza(giorni);
            return ResponseEntity.ok(vaccinazioni);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore del server");
        }
    }

    /**
     * Endpoint POST per inviare il promemoria di un vaccino
     * 
     * @param id l'ID della vaccinazione
     * @return messaggio di conferma
     */
    @PostMapping("/{id}/promemoria")
    public ResponseEntity<String> inviaPromemoria(@PathVariable Integer id) {
        try {
            String esito = vaccinazioniService.inviaPromemoriaVaccino(id);
            return ResponseEntity.ok(esito);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore nell'invio del promemoria");
        }
    }

    @PatchMapping("/{id}/elimina")
    public ResponseEntity<Void> eliminaVaccinazionePatch(@PathVariable Integer id) {
        vaccinazioniService.eliminaVaccinazione(id);
        return ResponseEntity.ok().build();
    }
}