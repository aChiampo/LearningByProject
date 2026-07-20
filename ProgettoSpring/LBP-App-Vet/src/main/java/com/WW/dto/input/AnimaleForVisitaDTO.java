package com.WW.dto.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AnimaleForVisitaDTO(
        @Valid @NotNull(message = "L'id dell'animale non può essere nullo") Integer id,
        @Valid @NotNull(message = "Il nome dell'animale non può essere nullo") String nome,
        @Valid @NotNull(message = "La specie dell'animale non può essere nullo") String specie,
        @Valid @NotNull(message = "La razza dell'animale non può essere nullo") String razza,
        @Valid @NotNull(message = "Il sesso dell'animale non può essere nullo") String sesso) {

}
