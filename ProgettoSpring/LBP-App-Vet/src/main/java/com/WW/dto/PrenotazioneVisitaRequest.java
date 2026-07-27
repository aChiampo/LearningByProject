package com.WW.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record PrenotazioneVisitaRequest(
        @NotNull(message = "L'animale e obbligatorio.") Integer animaleId,
        @NotNull(message = "Il veterinario e obbligatorio.") Integer veterinarioId,
        @NotNull(message = "Il tipo di visita e obbligatorio.") Integer tipoVisitaId,
        @NotNull(message = "Lo slot e obbligatorio.") @Future(message = "Lo slot deve essere futuro.") LocalDateTime dataVisita,
        String note) {
}
