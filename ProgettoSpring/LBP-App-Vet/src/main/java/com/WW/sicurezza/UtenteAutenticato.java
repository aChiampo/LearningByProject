package com.WW.sicurezza;

import org.springframework.security.core.AuthenticatedPrincipal;

/**
 * Principal autenticato salvato dentro l'oggetto Authentication di Spring Security.
 *
 * @param id id dell'utente autenticato
 * @param email email dell'utente autenticato
 */
public record UtenteAutenticato(
        Integer id,
        String email
) implements AuthenticatedPrincipal {

    /**
     * Mantiene Authentication.getName() compatibile con il codice esistente.
     *
     * @return id utente come stringa
     */
    @Override
    public String getName() {
        return id.toString();
    }
}
