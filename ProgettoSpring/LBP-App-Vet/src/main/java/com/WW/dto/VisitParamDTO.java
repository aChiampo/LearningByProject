package com.WW.dto;

import java.time.LocalDateTime;

import com.WW.enums.VisitaStato;
import com.fasterxml.jackson.annotation.JsonAlias;

public record VisitParamDTO(
        @JsonAlias({ "Date", "data", "dataVisita" })
        LocalDateTime date,

        @JsonAlias({ "DoctorID", "doctorId", "idVeterinario", "veterinarioId" })
        Integer doctorID,

        @JsonAlias({ "ClientID", "clientId", "idCliente", "clienteId" })
        Integer clientID,

        @JsonAlias({ "AnimalID", "animalId", "idAnimale", "animaleId" })
        Integer animalID,

        @JsonAlias({ "TipoVisitaID", "tipoVisitaId", "idTipoVisita" })
        Integer tipoVisitaID,

        @JsonAlias({ "PagamentoID", "pagamentoId", "idPagamento" })
        Integer pagamentoID,

        VisitaStato stato,

        Boolean pagata) {
}
