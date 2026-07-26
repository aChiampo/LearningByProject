package com.WW.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.WW.services.AnimaleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/animali")
@RequiredArgsConstructor
//TODO: Aggiungere le validazioni (neccessario il DTO)
public class AnimaleController {

    private final AnimaleService animaleService;

    // 1. LEGGI TUTTI GLI ANIMALI -> GET
    // http://localhost:8080/api/animali/leggiTutti
    
    @GetMapping("/leggiTutti")
    public ResponseEntity<List<Animale>> leggiTutti() {
        return ResponseEntity.ok(animaleService.ottieniTutti());
    }

    // 2. LEGGI SINGOLO ANIMALE -> GET http://localhost:8080/api/animali/leggi/12
    @GetMapping("/leggi/{id}")
    public ResponseEntity<Animale> leggi(@PathVariable Integer id, Authentication Autenticazione) {
        int userId = Integer.parseInt(Autenticazione.getName());

        boolean isOwner = animaleService.ottieniPerUtente(userId).stream()
                .anyMatch(animale -> animale.getId().equals(id));

        if (!isOwner && !isVeterinario(Autenticazione)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(animaleService.ottieniPerId(id));
    }

    private boolean isVeterinario(Authentication autenticazione) {
        return autenticazione.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_VETERINARIO"));
    }

    // 3. LEGGI GLI ANIMALI DI UN UTENTE -> GET
    // http://localhost:8080/api/animali/leggiPerUtente/5
    @GetMapping("/leggiPerUtente/{utenteId}")
    public ResponseEntity<List<Animale>> leggiPerUtente(@PathVariable Integer utenteId) {
        return ResponseEntity.ok(animaleService.ottieniPerUtente(utenteId));
    }

    // 4. REGISTRA UN NUOVO ANIMALE -> POST http://localhost:8080/api/animali/crea
    @PostMapping("/crea")
    public ResponseEntity<Animale> crea(@RequestBody Animale animale) {
        Animale nuovoAnimale = animaleService.creaAnimale(animale);
        return new ResponseEntity<>(nuovoAnimale, HttpStatus.CREATED);
    }

    // 5. MODIFICA UN ANIMALE -> PATCH http://localhost:8080/api/animali/modifica/12
    @PatchMapping("/modifica/{id}")
    public ResponseEntity<Animale> modifica(@PathVariable Integer id, @RequestBody Animale animale) {
        Animale animaleAggiornato = animaleService.aggiornaAnimale(id, animale);
        return ResponseEntity.ok(animaleAggiornato);
    }

    // 6. ELIMINA UN ANIMALE -> DELETE http://localhost:8080/api/animali/elimina/12
    @DeleteMapping("/elimina/{id}")
    public ResponseEntity<Void> elimina(@PathVariable Integer id) {
        animaleService.eliminaAnimale(id);
        return ResponseEntity.noContent().build();
    }
}
