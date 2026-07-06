package com.WW.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vaccinazioni")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vaccinazioni {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipiVaccino idTipoVaccino;

    @Column(name = "data_vaccinazione", nullable = true)
    private LocalDateTime dataVaccinazione;

    @Column(name = "lotto", nullable = true)
    private String lotto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_animale", nullable = false)
    private Animale idAnimale;
}
