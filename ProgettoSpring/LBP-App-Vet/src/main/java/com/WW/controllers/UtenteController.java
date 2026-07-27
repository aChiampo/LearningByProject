package com.WW.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.bind.annotation.RestController;

import com.WW.dto.UtenteDto;
import com.WW.entities.Utente;
import com.WW.services.UtenteService;

import lombok.RequiredArgsConstructor;

/**
 * REST controller per la gestione degli utenti.
 */
@RestController
@RequestMapping({"/api/utenti", "/api/utente"})
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UtenteController {

    private final UtenteService utenteService;

    /**
     * Restituisce tutti gli utenti.
     *
     * @return elenco degli utenti
     */
    @GetMapping("/ottieniTutti")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST')")
    public ResponseEntity<List<Utente>> ottieniTutti() {
        return ResponseEntity.ok(utenteService.ottieniTutti());
    }

    @GetMapping("/ottieniVeterinari")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<Utente>> ottieniVeterinari() {
        return ResponseEntity.ok(utenteService.ottieniVeterinariAttivi());
    }

    /**
     * Restituisce un utente a partire dall'id.
     *
     * @param id identificativo dell'utente
     * @return utente trovato
     */
    @GetMapping("/ottieni/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<Utente> ottieni(@PathVariable Integer id, Authentication authentication) {
        if (!canAccessUser(id, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(utenteService.ottieniPerId(id));
    }

    /**
     * Crea un nuovo utente.
     *
     * @param utente dati dell'utente da salvare
     * @return utente creato
     */
    @PostMapping("/aggiungi")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Utente> crea(@RequestBody Utente utente) {
        Utente nuovoUtente = utenteService.aggiungiUtente(utente);
        return new ResponseEntity<>(nuovoUtente, HttpStatus.CREATED);
    }

    /**
     * Aggiorna un utente esistente.
     *
     * @param id identificativo dell'utente da aggiornare
     * @param utente nuovi dati da applicare
     * @return utente aggiornato
     */
    @PatchMapping("/modifica/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<Utente> modifica(@PathVariable Integer id, @RequestBody UtenteDto utente, Authentication authentication) {
        if (!canAccessUser(id, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Utente utenteAggiornato = utenteService.modificaUtente(id, utente);
        return ResponseEntity.ok(utenteAggiornato);
    }

    /**
     * Elimina un utente esistente.
     *
     * @param id identificativo dell'utente da eliminare
     * @return risposta senza contenuto
     */
    @DeleteMapping("/elimina/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> elimina(@PathVariable Integer id) {
        utenteService.eliminaUtente(id);
        return ResponseEntity.noContent().build();
    }

    private boolean canAccessUser(Integer id, Authentication authentication) {
        if (hasRole(authentication, "ROLE_ADMIN")) {
            return true;
        }

        return Integer.valueOf(authentication.getName()).equals(id);
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }
}
