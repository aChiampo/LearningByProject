package com.WW.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.WW.entities.Visita;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record VisitaDto(
        @Valid @NotNull(message = "L'animale non può essere nullo") AnimaleDto animale,
        @Valid @NotNull(message = "Il tipo di visita non può essere nullo") TipoVisitaDto tipoVisita,
        @Valid @NotNull(message = "Il veterinario non può essere nullo") UtenteDto veterinario,
        @NotNull(message = "La data della visita non può essere nulla") LocalDate data,
        @NotNull(message = "La fascia oraria non può essere nulla") String fasciaOraria,
        @Valid PagamentoDto pagamento,
        String note) {
    // Computes the start date and time based on `data` and `fasciaOraria`
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

    public static VisitaDto fromEntity(Visita visita) {
        return new VisitaDto(
                new AnimaleDto(visita.getAnimale().getId()),
                new TipoVisitaDto(visita.getTipoVisita().getId()),
                new UtenteDto(visita.getVeterinario().getId()),
                visita.getDataVisita().toLocalDate(),
                visita.getDataVisita().toLocalTime().isBefore(LocalTime.of(12, 30)) ? "Mattina" : "Pomeriggio",
                visita.getPagamento() != null ? new PagamentoDto(visita.getPagamento().getId()) : null,
                null // Assuming note is not present in the entity
        );
    }
}