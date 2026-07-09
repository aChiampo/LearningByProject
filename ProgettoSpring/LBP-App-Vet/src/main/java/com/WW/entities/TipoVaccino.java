package com.WW.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TIPI_VACCINO")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class TipoVaccino {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "Tipologia", nullable = false)
    private String tipologia;
    @Column(name = "Durata", nullable = false) //
    private int durata;
    @Column(name = "Note", nullable = true)
    private String note;
    
    @Column(name = "isDeleted", nullable = false)
    private Boolean isDeleted;
}
