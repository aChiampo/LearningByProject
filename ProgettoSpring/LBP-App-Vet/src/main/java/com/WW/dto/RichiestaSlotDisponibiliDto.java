package com.WW.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record RichiestaSlotDisponibiliDto(
        @NotNull(message = "L'animale e obbligatorio.") Integer animaleId,
        @NotNull(message = "Il veterinario e obbligatorio.") Integer veterinarioId,
        @NotNull(message = "Il tipo di visita e obbligatorio.") Integer tipoVisitaId,
        @NotNull(message = "La data e obbligatoria.") @Future(message = "La data deve essere futura.") LocalDate data) {
}
