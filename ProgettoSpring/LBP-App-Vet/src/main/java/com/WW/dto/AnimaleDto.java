package com.WW.dto;

import jakarta.validation.constraints.NotNull;

public record AnimaleDto(
    @NotNull Integer id
) {
}