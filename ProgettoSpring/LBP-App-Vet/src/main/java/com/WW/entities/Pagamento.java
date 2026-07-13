package com.WW.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "pagamenti")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "data", nullable = false)
    private LocalDate data;

    @Column(name = "tipo_pagamento", length = 30)
    private String tipoPagamento;

    @Column(name = "stato", length = 30)
    private String stato;

    @Column(name = "importo_totale", precision = 8, scale = 2, nullable = false)
    private BigDecimal importoTotale;

    @ManyToOne
    @JoinColumn(name = "id_utente", referencedColumnName = "id")
    private Utente utente;

    @ManyToOne
    @JoinColumn(name = "riferimento_file", referencedColumnName = "id", nullable = true)
    private FileReferences riferimentoFile;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
