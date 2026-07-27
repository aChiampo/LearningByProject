package com.WW.dto;

import java.time.LocalDateTime;

public record SlotDisponibileDto(
        LocalDateTime inizio,
        LocalDateTime fine,
        String label) {
}
