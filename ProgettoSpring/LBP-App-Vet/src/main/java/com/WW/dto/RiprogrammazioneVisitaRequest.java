package com.WW.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public record RiprogrammazioneVisitaRequest(
        @NotNull(message = "Lo slot e obbligatorio.") @Future(message = "Lo slot deve essere futuro.") LocalDateTime dataVisita) {
}
