package com.WW.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AnimaleDTO(
        @Valid @NotNull(message = "L'id dell'animale non può essere nullo") Integer id,
        @Valid @NotNull(message = "Il nome dell'animale non può essere nullo") String nome,
        @Valid String specie,
        @Valid String razza,
        @Valid String sesso,
        @Valid Double peso,
        @Valid String microchip,
        @Valid String note,
        @Valid @NotNull(message = "Lo stato di eliminazione dell'animale non può essere nullo") boolean isDeleted,
        @Valid @NotNull(message = "La data di nascita dell'animale non può essere nulla") String dataNascita,
        @Valid @NotNull(message = "L'id del cliente associato all'animale non può essere nullo") Integer idCliente) {
}
