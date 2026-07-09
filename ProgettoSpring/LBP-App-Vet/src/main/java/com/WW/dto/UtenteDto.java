package com.WW.dto;

import jakarta.validation.constraints.NotNull;

public record UtenteDto(
    @NotNull Integer id
) {
}