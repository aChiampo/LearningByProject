package com.WW.autenticazione;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.WW.sicurezza.UtenteAutenticato;

import lombok.RequiredArgsConstructor;

/**
 * Controller REST che gestisce le operazioni di autenticazione.
 */
@RestController
@RequestMapping({"/api/auth", "/api/autenticazione"})
@RequiredArgsConstructor
public class AutenticazioneController {

    private final AutenticazioneService autenticazioneService;

    /**
     * Autentica un utente tramite email e password e restituisce un token Bearer.
     *
     * @param richiesta dati di accesso inviati dal client
     * @return risposta contenente il JWT se le credenziali sono valide
     */
    @PostMapping("/login")
    public ResponseEntity<RispostaLogin> accedi(@RequestBody RichiestaLogin richiesta) {
        return ResponseEntity.ok(autenticazioneService.accedi(richiesta));
    }

    @PostMapping("/signin")
    public ResponseEntity<RispostaLogin> registrazione(@RequestBody RichiestaRegistrazione richiesta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autenticazioneService.registra(richiesta));
    }

    @GetMapping("/me")
    public ResponseEntity<ProfiloAutenticato> profiloCorrente(
            @AuthenticationPrincipal UtenteAutenticato utenteAutenticato) {
        return ResponseEntity.ok(autenticazioneService.ottieniProfiloAutenticato(utenteAutenticato.id()));
    }
}
