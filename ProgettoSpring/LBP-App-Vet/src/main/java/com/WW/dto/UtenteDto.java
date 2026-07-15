package com.WW.dto;

public record UtenteDto(
    String nome,
    String cognome,
    String email,
    String telefono,
    String indirizzo,
    String citta
) {
}