package com.WW.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record RichiestaValutazioneAnimaleDettaglioDto(
        Integer id,
        String nome,
        String specie,
        String razza,
        String sesso,
        LocalDate dataNascita,
        Double peso,
        String microchip,
        String note,
        String descrizione,
        LocalDateTime dataRichiesta,
        Integer utenteId,
        String nomeCliente,
        String emailCliente,
        String telefonoCliente) {
}
