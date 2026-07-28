package com.WW.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.WW.entities.Animale;
import com.WW.entities.Utente;
import com.WW.services.AnimaleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/animali")
@RequiredArgsConstructor
//TODO: Aggiungere le validazioni (neccessario il DTO)
/**
 * Controller REST per la gestione degli animali e dei controlli di accesso sui proprietari.
 */
public class AnimaleController {

    private final AnimaleService animaleService;

    // 1. LEGGI TUTTI GLI ANIMALI -> GET
    // http://localhost:8080/api/animali/leggiTutti
    /**
     * Gestisce la richiesta HTTP per leggiTutti.
     *
     * @return risultato dell'operazione
     */
    
    @GetMapping("/leggiTutti")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST')")
    public ResponseEntity<List<Animale>> leggiTutti() {
        return ResponseEntity.ok(animaleService.ottieniTutti());
    }

    // 2. LEGGI SINGOLO ANIMALE -> GET http://localhost:8080/api/animali/leggi/12
    /**
     * Gestisce la richiesta HTTP per leggi.
     *
     * @param id parametro richiesto dall'operazione
     * @param autenticazione parametro richiesto dall'operazione
     * @return risultato dell'operazione
     */
    @GetMapping("/leggi/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<Animale> leggi(@PathVariable Integer id, Authentication autenticazione) {
        int userId = Integer.parseInt(autenticazione.getName());

        boolean isOwner = isCliente(autenticazione) && animaleService.ottieniPerUtente(userId).stream()
                .anyMatch(animale -> animale.getId().equals(id));

        if (!isOwner && !isPrivilegedAnimalReader(autenticazione)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(animaleService.ottieniPerId(id));
    }

    private boolean isPrivilegedAnimalReader(Authentication autenticazione) {
        return autenticazione.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_VETERINARIO")
                        || authority.equals("ROLE_RECEPTIONIST")
                        || authority.equals("ROLE_ADMIN"));
    }

    private boolean isCliente(Authentication autenticazione) {
        return autenticazione.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_CLIENTE"));
    }

    // 3. LEGGI GLI ANIMALI DI UN UTENTE -> GET
    // http://localhost:8080/api/animali/leggiPerUtente/5
    /**
     * Gestisce la richiesta HTTP per leggiPerUtente.
     *
     * @param utenteId parametro richiesto dall'operazione
     * @param autenticazione parametro richiesto dall'operazione
     * @return risultato dell'operazione
     */
    @GetMapping("/leggiPerUtente/{utenteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<Animale>> leggiPerUtente(@PathVariable Integer utenteId, Authentication autenticazione) {
        if (isCliente(autenticazione) && !Integer.valueOf(autenticazione.getName()).equals(utenteId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(animaleService.ottieniPerUtente(utenteId));
    }

    // 4. REGISTRA UN NUOVO ANIMALE -> POST http://localhost:8080/api/animali/crea
    /**
     * Gestisce la richiesta HTTP per crea.
     *
     * @param animale parametro richiesto dall'operazione
     * @param autenticazione parametro richiesto dall'operazione
     * @return risultato dell'operazione
     */
    @PostMapping("/crea")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<Animale> crea(@RequestBody Animale animale, Authentication autenticazione) {
        if (isCliente(autenticazione)) {
            Integer userId = Integer.valueOf(autenticazione.getName());

            if (animale.getUtente() != null && !userId.equals(animale.getUtente().getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Utente proprietario = new Utente();
            proprietario.setId(userId);
            animale.setUtente(proprietario);
        }

        Animale nuovoAnimale = animaleService.creaAnimale(animale);
        return new ResponseEntity<>(nuovoAnimale, HttpStatus.CREATED);
    }

    // 5. MODIFICA UN ANIMALE -> PATCH http://localhost:8080/api/animali/modifica/12
    /**
     * Gestisce la richiesta HTTP per modifica.
     *
     * @param id parametro richiesto dall'operazione
     * @param animale parametro richiesto dall'operazione
     * @return risultato dell'operazione
     */
    @PatchMapping("/modifica/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<Animale> modifica(@PathVariable Integer id, @RequestBody Animale animale) {
        Animale animaleAggiornato = animaleService.aggiornaAnimale(id, animale);
        return ResponseEntity.ok(animaleAggiornato);
    }

    // 6. ELIMINA UN ANIMALE -> DELETE http://localhost:8080/api/animali/elimina/12
    /**
     * Gestisce la richiesta HTTP per elimina.
     *
     * @param id parametro richiesto dall'operazione
     * @return risultato dell'operazione
     */
    @DeleteMapping("/elimina/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> elimina(@PathVariable Integer id) {
        animaleService.eliminaAnimale(id);
        return ResponseEntity.noContent().build();
    }
}
