package com.WW.autenticazione;

import com.WW.entities.Utente;
import com.WW.repositories.UtenteRepo;
import com.WW.sicurezza.ServizioJwt;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller REST che gestisce le operazioni di autenticazione.
 */
@RestController
@RequestMapping({"/api/auth", "/api/autenticazione"})
@RequiredArgsConstructor
public class AutenticazioneController {

    private final UtenteRepo utenteRepo;
    private final PasswordEncoder codificatorePassword;
    private final ServizioJwt servizioJwt;

    /**
     * Autentica un utente tramite email e password e restituisce un token Bearer.
     *
     * @param richiesta dati di accesso inviati dal client
     * @return risposta contenente il JWT se le credenziali sono valide
     */
    @PostMapping("/login")
    public ResponseEntity<RispostaLogin> accedi(@RequestBody RichiestaLogin richiesta) {
        Utente utente = utenteRepo.findByEmail(richiesta.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide."));

        if (!codificatorePassword.matches(richiesta.password(), utente.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide.");
        }

        String token = servizioJwt.creaToken(utente);
        RispostaLogin risposta = new RispostaLogin(token, "Bearer", servizioJwt.ottieniScadenzaSecondi());

        return ResponseEntity.ok(risposta);
    }
}
