package com.WW.dto;

public record TipoVaccinoRequest(
        String tipologia,
        Integer durata,
        String note
) {
}
