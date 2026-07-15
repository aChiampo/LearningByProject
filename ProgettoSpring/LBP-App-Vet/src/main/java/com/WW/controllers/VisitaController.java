package com.WW.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.WW.dto.VisitParamDTO;
import com.WW.dto.VisitaDto;
import com.WW.entities.Visita;
import com.WW.sicurezza.UtenteAutenticato;
import com.WW.services.VisitaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/visite")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VisitaController {

    private final VisitaService visitaService;

    /**
     * Restituisce tutte le visite.
     *
     * @return elenco delle visite
     */
    @GetMapping("/ottieniTutte")
    public ResponseEntity<List<Visita>> ottieniTutte(Authentication authentication) {
        return ResponseEntity.ok(visitaService.getAllVisita(getClienteIdIfCliente(authentication)));
    }

    /**
     * Restituisce una visita a partire dall'id.
     *
     * @param id identificativo della visita
     * @return visita trovata
     */
    @GetMapping("/ottieni/{id}")
    public ResponseEntity<Visita> ottieni(@PathVariable Integer id) {
        return ResponseEntity.ok(visitaService.getVisitaById(id));
    }

    /**
     * Restituisce le visite associate a un tipo visita.
     *
     * @param idTipoVisita identificativo del tipo visita
     * @return visite filtrate per tipo
     */
    @GetMapping("/ottieniPerTipo/{idTipoVisita}")
    public ResponseEntity<List<Visita>> ottieniPerTipo(@PathVariable Integer idTipoVisita) {
        return ResponseEntity.ok(visitaService.getVisiteByTipoVisita(idTipoVisita));
    }

    /**
     * Restituisce le visite associate a un veterinario.
     *
     * @param idVeterinario identificativo del veterinario
     * @return visite filtrate per veterinario
     */
    @GetMapping("/ottieniPerVeterinario/{idVeterinario}")
    public ResponseEntity<List<Visita>> ottieniPerVeterinario(@PathVariable Integer idVeterinario) {
        return ResponseEntity.ok(visitaService.getVisiteByVeterinario(idVeterinario));
    }

    /**
     * Restituisce le visite associate a un animale.
     *
     * @param idAnimale identificativo dell'animale
     * @return visite filtrate per animale
     */
    @GetMapping("/ottieniPerAnimale/{idAnimale}")
    public ResponseEntity<List<Visita>> ottieniPerAnimale(@PathVariable Integer idAnimale) {
        return ResponseEntity.ok(visitaService.getVisiteByAnimale(idAnimale));
    }

    /**
     * Restituisce la visita collegata a un pagamento.
     *
     * @param idPagamento identificativo del pagamento
     * @return visita trovata
     */
    @GetMapping("/ottieniPerPagamento/{idPagamento}")
    public ResponseEntity<Visita> ottieniPerPagamento(@PathVariable Integer idPagamento) {
        return ResponseEntity.ok(visitaService.getVisitaByPagamento(idPagamento));
    }

    /**
     * Restituisce tutte le visite pagate.
     *
     * @return visite pagate
     */
    @GetMapping("/ottieniPagate")
    public ResponseEntity<List<Visita>> ottieniPagate(Authentication authentication) {
        return ResponseEntity.ok(visitaService.OttieniVisitePagate(getClienteIdIfCliente(authentication)));
    }

    /**
     * Restituisce tutte le visite non pagate.
     *
     * @return visite non pagate
     */
    @GetMapping("/ottieniNonPagate")
    public ResponseEntity<List<Visita>> ottieniNonPagate(Authentication authentication) {
        return ResponseEntity.ok(visitaService.OttieniVisiteNonPagate(getClienteIdIfCliente(authentication)));
    }

    /**
     * Restituisce tutte le visite pagate di un animale.
     *
     * @param idAnimale identificativo dell'animale
     * @return visite pagate dell'animale
     */
    @GetMapping("/ottieniPagatePerAnimale/{idAnimale}")
    public ResponseEntity<List<Visita>> ottieniPagatePerAnimale(@PathVariable int idAnimale) {
        return ResponseEntity.ok(visitaService.OttieniVisitePagatebyAnimale(idAnimale));
    }

    /**
     * Restituisce tutte le visite non pagate di un animale.
     *
     * @param idAnimale identificativo dell'animale
     * @return visite non pagate dell'animale
     */
    @GetMapping("/ottieniNonPagatePerAnimale/{idAnimale}")
    public ResponseEntity<List<Visita>> ottieniNonPagatePerAnimale(@PathVariable int idAnimale) {
        return ResponseEntity.ok(visitaService.OttieniVisiteNonPagateByAnimale(idAnimale));
    }

    /**
     * Restituisce le visite filtrate dai parametri specificati.
     *
     * @param params parametri opzionali di filtro
     * @return visite filtrate
     */
    @PostMapping("/params")
    public ResponseEntity<List<VisitaDto>> ottieniPerParametri(@RequestBody(required = false) VisitParamDTO params) {
        return ResponseEntity.ok(visitaService.getVisiteByParams(params));
    }

    /**
     * Crea una nuova visita.
     *
     * @param visita dati della visita da salvare
     * @return visita creata
     */
    @PostMapping("/prenotazione")
    public ResponseEntity<Visita> prenota(@Valid @RequestBody VisitaDto visita) {
        Visita nuovaVisita = visitaService.createVisita(visita);
        return new ResponseEntity<Visita>(nuovaVisita, HttpStatus.CREATED);
    }

    /**
     * Aggiorna le note di una visita.
     *
     * @param id   identificativo della visita
     * @param note nuove note da salvare
     * @return visita aggiornata
     */
    @PatchMapping("/aggiornaNote/{id}")
    public ResponseEntity<Visita> aggiornaNote(
            @PathVariable Integer id,
            @RequestParam String note) {
        return ResponseEntity.ok(visitaService.updateVisitaNote(id, note));
    }

    /**
     * Aggiorna lo stato di pagamento di una visita.
     *
     * @param id     identificativo della visita
     * @param pagamento nuovo stato di pagamento
     * @return visita aggiornata
     */
    @PatchMapping("/aggiornaPagato/{id}")
    public ResponseEntity<Visita> aggiornaPagato( @PathVariable Integer id, @RequestParam("pagamento") Integer pagamentoId) {
        return ResponseEntity.ok(visitaService.updateVisitaPagato(id, pagamentoId));
    }

    /**
     * Elimina una visita esistente.
     *
     * @param id identificativo della visita da eliminare
     * @return risposta senza contenuto
     */
    @DeleteMapping("/elimina/{id}")
    public ResponseEntity<Void> elimina(@PathVariable Integer id) {
        visitaService.deleteVisita(id);
        return ResponseEntity.noContent().build();
    }

    private Integer getClienteIdIfCliente(Authentication authentication) {
        if (!isCliente(authentication)) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UtenteAutenticato utenteAutenticato) {
            return utenteAutenticato.id();
        }

        return Integer.valueOf(authentication.getName());
    }

    private boolean isCliente(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_CLIENTE"));
    }
}
