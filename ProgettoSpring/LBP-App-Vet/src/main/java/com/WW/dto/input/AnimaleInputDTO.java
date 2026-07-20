package com.WW.dto.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AnimaleInputDTO(
        @Valid @NotNull(message = "Il nome dell'animale non può essere nullo") String nome,
        @Valid @NotNull(message = "La specie dell'animale non può essere nullo") String specie,
        String razza,
        String sesso,
        String dataNascita,
        Double peso,
        String microchip,
        String note) {
}
