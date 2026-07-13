package com.WW.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record VisitaDto(
    @Valid @NotNull(message = "L'animale non può essere nullo") String animale,
    @Valid @NotNull(message = "Il tipo di visita non può essere nullo") String tipoVisita,
    @Valid @NotNull(message = "Il veterinario non può essere nullo") String veterinario,
    @NotNull(message = "La data della visita non può essere nulla") LocalDate data,
    @NotNull(message = "La fascia oraria non può essere nulla") String fasciaOraria,
    @Valid PagamentoDto pagamento,
    String note
) {
    // Computes the start date and time based on `data` and `fasciaOraria`
    public LocalDateTime getStartDateTime() {
        LocalTime startTime;
        if (fasciaOraria.contains("Mattina")) {
            startTime = LocalTime.of(9, 0); // 09:00
        } else if (fasciaOraria.contains("Pomeriggio")) {
            startTime = LocalTime.of(14, 30); // 14:30
        } else {
            throw new IllegalArgumentException("Invalid time slot: " + fasciaOraria);
        }
        return LocalDateTime.of(data, startTime);
    }

    // Computes the end date and time based on `data` and `fasciaOraria`
    public LocalDateTime getEndDateTime() {
        LocalTime endTime;
        if (fasciaOraria.contains("Mattina")) {
            endTime = LocalTime.of(12, 30); // 12:30
        } else if (fasciaOraria.contains("Pomeriggio")) {
            endTime = LocalTime.of(18, 30); // 18:30
        } else {
            throw new IllegalArgumentException("Invalid time slot: " + fasciaOraria);
        }
        return LocalDateTime.of(data, endTime);
    }
}