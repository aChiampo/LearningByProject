package com.WW.entities;

import java.time.LocalDate;
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
@Table(name = "RICHIESTE_VALUTAZIONE_ANIMALI")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RichiestaValutazioneAnimale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NOME", length = 30, nullable = false)
    private String nome;

    @Column(name = "SPECIE", length = 30, nullable = false)
    private String specie;

    @Column(name = "RAZZA", length = 40, nullable = false)
    private String razza;

    @Column(name = "SESSO", length = 10, nullable = false)
    private String sesso;

    @Column(name = "DATA_NASCITA", nullable = false)
    private LocalDate dataNascita;

    @Column(name = "PESO", nullable = false)
    private Double peso;

    @Column(name = "MICROCHIP", length = 15)
    private String microchip;

    @Column(name = "NOTE", columnDefinition = "TEXT")
    private String note;

    @Column(name = "DESCRIZIONE", columnDefinition = "TEXT", nullable = false)
    private String descrizione;

    @Column(name = "DATA_RICHIESTA", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dataRichiesta = LocalDateTime.now();

    @Column(name = "IS_DELETED", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UTENTE", nullable = false)
    private Utente utente;
}
