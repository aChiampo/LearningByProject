package com.WW.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DelayNotificationRequest(
        @NotNull(message = "L'id della visita e obbligatorio.") Integer visitaId,
        @NotNull(message = "Il ritardo e obbligatorio.") @Min(value = 1, message = "Il ritardo deve essere almeno di 1 minuto.") Integer delayMinutes) {
}
