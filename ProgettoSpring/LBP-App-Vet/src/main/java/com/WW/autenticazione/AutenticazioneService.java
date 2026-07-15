package com.WW.autenticazione;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.WW.entities.Ruolo;
import com.WW.entities.Utente;
import com.WW.services.AziendaService;
import com.WW.services.RuoloService;
import com.WW.services.UtenteService;
import com.WW.sicurezza.ServizioJwt;

import lombok.RequiredArgsConstructor;

/**
 * Servizio che gestisce login e registrazione degli utenti.
 */
@Service
@RequiredArgsConstructor
public class AutenticazioneService {

    private final RuoloService ruoloService;
    private final UtenteService utenteService;
    private final AziendaService aziendaService;
    private final PasswordEncoder codificatorePassword;
    private final ServizioJwt servizioJwt;

    public RispostaLogin accedi(RichiestaLogin richiesta) {
        Utente utente = utenteService.ottieniPerEmail(richiesta.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide."));

        if (!codificatorePassword.matches(richiesta.password(), utente.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenziali non valide.");
        }

        return creaRispostaLogin(utente);
    }

    public RispostaLogin registra(RichiestaRegistrazione richiesta) {
        validaRegistrazione(richiesta);

        Utente nuovoUtente = new Utente();
        nuovoUtente.setEmail(richiesta.email());
        nuovoUtente.setPasswordHash(richiesta.passwordInChiaro());
        nuovoUtente.setNome(richiesta.nome());
        nuovoUtente.setCognome(richiesta.cognome());
        nuovoUtente.setCodiceFiscale(richiesta.codiceFiscale());
        nuovoUtente.setTelefono(richiesta.telefono());

        if (richiesta.indirizzo() != null && !richiesta.indirizzo().isBlank()) {
            nuovoUtente.setIndirizzo(richiesta.indirizzo());
        }

        if (richiesta.citta() != null && !richiesta.citta().isBlank()) {
            nuovoUtente.setCitta(richiesta.citta());
        }

        if (richiesta.aziendaId() != null) {
            nuovoUtente.setAzienda(aziendaService.ottieniPerId(richiesta.aziendaId()));
        }

        Ruolo ruolo = ruoloService.ottieniPerRuolo("CLIENTE")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ruolo CLIENTE non trovato"));
        nuovoUtente.setRuolo(ruolo);

        Utente utenteSalvato = utenteService.aggiungiUtente(nuovoUtente);
        return creaRispostaLogin(utenteSalvato);
    }

    private RispostaLogin creaRispostaLogin(Utente utente) {
        String token = servizioJwt.creaToken(utente);
        return new RispostaLogin(token, "Bearer", servizioJwt.ottieniScadenzaSecondi(), creaProfilo(utente));
    }

    public ProfiloAutenticato ottieniProfiloAutenticato(Integer idUtente) {
        return creaProfilo(utenteService.ottieniPerId(idUtente));
    }

    private ProfiloAutenticato creaProfilo(Utente utente) {
        return new ProfiloAutenticato(
                utente.getId(),
                utente.getEmail(),
                utente.getRuolo().getRuolo(),
                utente.getNome(),
                utente.getCognome(),
                utente.getTelefono(),
                utente.getIndirizzo(),
                utente.getCitta()
        );
    }

    private void validaRegistrazione(RichiestaRegistrazione richiesta) {
        if (richiesta == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dati di registrazione mancanti");
        }
        if (campoVuoto(richiesta.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email obbligatoria");
        }
        if (campoVuoto(richiesta.passwordInChiaro())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password obbligatoria");
        }
        if (campoVuoto(richiesta.nome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome obbligatorio");
        }
        if (campoVuoto(richiesta.cognome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cognome obbligatorio");
        }
        if (campoVuoto(richiesta.codiceFiscale())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Codice fiscale obbligatorio");
        }
        if (campoVuoto(richiesta.telefono())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Telefono obbligatorio");
        }
    }

    private boolean campoVuoto(String valore) {
        return valore == null || valore.isBlank();
    }
}
