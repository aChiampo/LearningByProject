package com.WW.dto;

import jakarta.validation.constraints.NotNull;

public record PagamentoDto(
    @NotNull Integer id
) {
}