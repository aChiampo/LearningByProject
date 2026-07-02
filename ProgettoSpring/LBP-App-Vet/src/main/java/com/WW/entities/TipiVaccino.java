package com.WW.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TIPI_VACCINO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TipiVaccino {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "Tipologia", nullable = false)
    private String tipologia;
    @Column(name = "Durata", nullable = false)//
    private int durata;
    @Column(name = "Note", nullable = true)
    private String note;
}
