package com.WW.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record VisitaDto(
        @Valid @NotNull(message = "L'animale non puo essere nullo") Integer animale,
        @Valid @NotNull(message = "Il tipo di visita non puo essere nullo") Integer tipoVisita,
        @Valid @NotNull(message = "Il veterinario non puo essere nullo") Integer veterinario,
        @NotNull(message = "La data della visita non puo essere nulla") LocalDate data,
        @NotNull(message = "La fascia oraria non puo essere nulla") String fasciaOraria,
        @Valid PagamentoDto pagamento,
        String note
) {
    public LocalDateTime getStartDateTime() {
        LocalTime startTime;
        if (fasciaOraria.contains("Mattina")) {
            startTime = LocalTime.of(9, 0);
        } else if (fasciaOraria.contains("Pomeriggio")) {
            startTime = LocalTime.of(14, 30);
        } else {
            throw new IllegalArgumentException("Invalid time slot: " + fasciaOraria);
        }
        return LocalDateTime.of(data, startTime);
    }

    public LocalDateTime getEndDateTime() {
        LocalTime endTime;
        if (fasciaOraria.contains("Mattina")) {
            endTime = LocalTime.of(12, 30);
        } else if (fasciaOraria.contains("Pomeriggio")) {
            endTime = LocalTime.of(18, 30);
        } else {
            throw new IllegalArgumentException("Invalid time slot: " + fasciaOraria);
        }
        return LocalDateTime.of(data, endTime);
    }
}
