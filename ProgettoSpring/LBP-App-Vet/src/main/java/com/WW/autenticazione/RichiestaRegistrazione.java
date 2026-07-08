package com.WW.autenticazione;

/**
 * Corpo della richiesta usata dall'endpoint di registrazione.
 */
public record RichiestaRegistrazione(
        String email,
        String password,
        String passwordHash,
        String nome,
        String cognome,
        String codiceFiscale,
        String telefono,
        String indirizzo,
        String citta,
        Integer aziendaId
) {
    public String passwordInChiaro() {
        if (password != null && !password.isBlank()) {
            return password;
        }
        return passwordHash;
    }
}
