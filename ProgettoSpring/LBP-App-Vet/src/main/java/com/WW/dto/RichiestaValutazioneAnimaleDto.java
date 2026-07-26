package com.WW.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RichiestaValutazioneAnimaleDto(
        @NotBlank String nome,
        @NotBlank String specie,
        @NotBlank String razza,
        @NotBlank String sesso,
        @NotNull LocalDate dataNascita,
        @NotNull @PositiveOrZero Double peso,
        String microchip,
        String note,
        @NotBlank String descrizione) {
}
