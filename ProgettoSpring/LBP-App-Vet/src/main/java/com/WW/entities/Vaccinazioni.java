package com.WW.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "VACCINAZIONI")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vaccinazioni {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "ID_TIPO_VACCINO", nullable = false)
    private TipiVaccino idTipoVaccino;

    @Column(name = "Data_Vaccinazione", nullable = true)
    private LocalDateTime dataVaccinazione;

    @Column(name = "Lotto", nullable = true)
    private String lotto;
    
    @OneToOne
    @JoinColumn(name = "ID_ANIMALE", nullable = false)
    private Animale idAnimale;
}
