package com.WW.autenticazione;

/**
 * Dati essenziali dell'utente autenticato usati dal frontend.
 *
 * @param id id dell'utente
 * @param email email dell'utente
 * @param ruolo ruolo applicativo salvato nel database
 * @param nome nome dell'utente
 * @param cognome cognome dell'utente
 * @param telefono telefono dell'utente
 * @param indirizzo indirizzo dell'utente
 * @param citta citta dell'utente
 */
public record ProfiloAutenticato(
        Integer id,
        String email,
        String ruolo,
        String nome,
        String cognome,
        String telefono,
        String indirizzo,
        String citta
) {
}
