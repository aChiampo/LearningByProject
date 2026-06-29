package com.WW.autenticazione;

/**
 * Corpo della richiesta usata dall'endpoint di login.
 *
 * @param email email dell'utente
 * @param password password in chiaro inserita dall'utente
 */
public record RichiestaLogin(
        String email,
        String password
) {
}
