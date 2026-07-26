package com.WW.dto;

import java.time.LocalDateTime;

import com.WW.entities.TipoVaccino;
import com.WW.entities.Vaccinazione;

public record VaccinazioneDto(
        Integer id,
        Integer tipoVaccinoId,
        String tipoVaccino,
        Integer durataMesi,
        Integer animaleId,
        LocalDateTime dataVaccinazione,
        String lotto
) {
    public static VaccinazioneDto fromEntity(Vaccinazione vaccinazione) {
        TipoVaccino tipoVaccino = vaccinazione.getIdTipoVaccino();

        return new VaccinazioneDto(
                vaccinazione.getId(),
                tipoVaccino != null ? tipoVaccino.getId() : null,
                tipoVaccino != null ? tipoVaccino.getTipologia() : null,
                tipoVaccino != null ? tipoVaccino.getDurata() : null,
                vaccinazione.getIdAnimale() != null ? vaccinazione.getIdAnimale().getId() : null,
                vaccinazione.getDataVaccinazione(),
                vaccinazione.getLotto()
        );
    }
}
