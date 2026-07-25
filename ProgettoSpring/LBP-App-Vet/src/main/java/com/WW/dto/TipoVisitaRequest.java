package com.WW.dto;

import java.math.BigDecimal;

public record TipoVisitaRequest(
        String nome,
        Integer durata,
        BigDecimal prezzo,
        Integer idCategoria,
        Integer idDottore,
        Boolean attivo
) {
}
