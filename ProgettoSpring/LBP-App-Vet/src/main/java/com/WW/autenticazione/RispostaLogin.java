package com.WW.autenticazione;

/**
 * Corpo della risposta restituita dopo un login corretto.
 *
 * @param token token JWT da usare nelle richieste protette
 * @param tipoToken tipo di token atteso nell'header Authorization
 * @param scadenzaSecondi durata del token espressa in secondi
 * @param utente profilo essenziale dell'utente autenticato
 */
public record RispostaLogin(
        String token,
        String tipoToken,
        long scadenzaSecondi,
        ProfiloAutenticato utente
) {
}
