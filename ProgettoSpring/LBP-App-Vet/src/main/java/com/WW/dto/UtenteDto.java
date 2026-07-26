package com.WW.dto;

public record UtenteDto(
        Integer id,
        String nome,
        String cognome,
        String email,
        String telefono,
        String indirizzo,
        String citta) {
    public UtenteDto(Integer id) {
        this(id, null, null, null, null, null, null);
    }

    public UtenteDto(String nome, String cognome, String email, String telefono, String indirizzo, String citta) {
        this(null, nome, cognome, email, telefono, indirizzo, citta);
    }
}
