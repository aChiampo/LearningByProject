package com.WW.dto.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record TipoVisitaInputDTO(
        @Valid @NotNull(message = "Il nome del tipo visita non può essere nullo") String nome) {
}
