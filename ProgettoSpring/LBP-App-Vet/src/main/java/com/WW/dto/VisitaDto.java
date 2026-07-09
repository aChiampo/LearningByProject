package com.WW.dto;

import java.time.LocalDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record VisitaDto(
    @NotNull(message = "La data della visita non può essere nulla")
    LocalDateTime dataVisita,
    @Valid
    @NotNull(message = "Il tipo di visita non può essere nullo")
    TipoVisitaDto tipoVisita,
    @Valid
    @NotNull(message = "L'animale non può essere nullo")
    AnimaleDto animale,
    @Valid
    @NotNull(message = "Il veterinario non può essere nullo")
    UtenteDto veterinario,
    @Valid
    PagamentoDto pagamento,
    String note
) {
}
