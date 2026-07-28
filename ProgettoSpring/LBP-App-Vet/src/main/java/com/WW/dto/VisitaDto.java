package com.WW.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.WW.entities.Visita;
import com.WW.enums.VisitaStato;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record VisitaDto(
        Integer id,
        @Valid @NotNull(message = "L'animale non puo essere nullo") AnimaleDto animale,
        @Valid @NotNull(message = "Il tipo di visita non puo essere nullo") TipoVisitaDto tipoVisita,
        @Valid @NotNull(message = "Il veterinario non puo essere nullo") UtenteDto veterinario,
        LocalDate data,
        String fasciaOraria,
        LocalDateTime dataVisita,
        @Valid PagamentoDto pagamento,
        String note,
        String notaPrivata,
        VisitaStato stato) {
    public VisitaDto(
            AnimaleDto animale,
            TipoVisitaDto tipoVisita,
            UtenteDto veterinario,
            LocalDate data,
            String fasciaOraria,
            PagamentoDto pagamento,
            String note) {
        this(null, animale, tipoVisita, veterinario, data, fasciaOraria, null, pagamento, note, null, null);
    }

    public LocalDateTime getStartDateTime() {
        if (dataVisita != null) {
            return dataVisita;
        }
        if (data == null) {
            throw new IllegalArgumentException("La data della visita non puo essere nulla.");
        }
        if (fasciaOraria == null || fasciaOraria.isBlank()) {
            throw new IllegalArgumentException("La fascia oraria non puo essere nulla.");
        }

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
        if (dataVisita != null) {
            return dataVisita;
        }
        if (data == null) {
            throw new IllegalArgumentException("La data della visita non puo essere nulla.");
        }
        if (fasciaOraria == null || fasciaOraria.isBlank()) {
            throw new IllegalArgumentException("La fascia oraria non puo essere nulla.");
        }

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
        var veterinario = visita.getVeterinario();

        return new VisitaDto(
                visita.getId(),
                new AnimaleDto(visita.getAnimale().getId()),
                new TipoVisitaDto(visita.getTipoVisita().getId()),
                new UtenteDto(
                        veterinario.getId(),
                        veterinario.getNome(),
                        veterinario.getCognome(),
                        veterinario.getEmail(),
                        veterinario.getTelefono(),
                        veterinario.getIndirizzo(),
                        veterinario.getCitta()),
                visita.getDataVisita().toLocalDate(),
                visita.getDataVisita().toLocalTime().isBefore(LocalTime.of(12, 30)) ? "Mattina" : "Pomeriggio",
                visita.getDataVisita(),
                visita.getPagamento() != null ? new PagamentoDto(visita.getPagamento().getId()) : null,
                visita.getNote(),
                visita.getNotaPrivata(),
                visita.getStato());
    }
}
