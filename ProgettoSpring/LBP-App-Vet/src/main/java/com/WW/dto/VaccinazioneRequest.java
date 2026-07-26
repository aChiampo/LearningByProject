package com.WW.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

public record VaccinazioneRequest(
        @NotNull Integer tipoVaccinoId,
        @NotNull Integer animaleId,
        @NotNull LocalDateTime dataVaccinazione,
        String lotto
) {
}
