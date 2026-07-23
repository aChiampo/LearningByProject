package com.WW.dto.input;

import com.WW.dto.AnimaleDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CreateVisitaDTO(
        @Valid @NotNull(message = "L'animale associato alla visita non può essere nullo") AnimaleDTO animale,
        @Valid @NotNull(message = "Il tipo visita associato alla visita non può essere nullo.") String tipoVisita,
        @Valid @NotNull(message = "Il veterinario associato alla visita non può essere nullo.") String veterinario,
        @Valid @NotNull(message = "La data della visita non può essere nulla.") String dataVisita,
        @Valid @NotNull(message = "L'orario della visita non può essere nullo.") String orarioVisita) {
}
