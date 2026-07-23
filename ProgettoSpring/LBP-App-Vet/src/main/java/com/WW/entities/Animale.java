package com.WW.entities;

import java.time.LocalDate;

import com.WW.dto.input.CreateAnimaleDTO;
import com.WW.dto.output.AnimaleOutputDTO;

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

/**
 * @author: Anqi Xu
 *          Modello di entità per la tabella ANIMALI
 *          Last update: 27/06/2026
 */

@Entity
@Table(name = "ANIMALI")
@Data // Potrebbe causare problemi se non gestito bene, meglio scrivere separatamente
      // i metodi che servono
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Animale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "NOME", length = 30, nullable = false)
    private String nome;

    @Column(name = "SPECIE", length = 15)
    private String specie;

    @Column(name = "RAZZA", length = 20)
    private String razza;

    @Column(name = "SESSO", length = 10)
    private String sesso;

    @Column(name = "DATA_NASCITA", nullable = false)
    private LocalDate dataNascita;

    @Column(name = "PESO")
    private Double peso;

    @Column(name = "MICROCHIP", length = 15, unique = true)
    private String microchip;

    @Column(name = "NOTE", columnDefinition = "TEXT")
    private String note;

    @Column(name = "isDeleted", nullable = false)
    private Boolean isDeleted;

    @ManyToOne(fetch = FetchType.LAZY) // Evita di caricare l'utente se non serve
    @JoinColumn(name = "ID_UTENTE", nullable = false)
    private Utente utente;

    /**
     * @author: Cristian Pappalardo
     * @description: Metodo statico per convertire un DTO in un'entità Animale
     * @param dto
     * @param utente
     * @return Animale entity created from CreateAnimaleDTO
     */
    public static Animale fromDTO(CreateAnimaleDTO dto, Utente utente) {
        return Animale.builder()
                .nome(dto.nome())
                .specie(dto.specie())
                .razza(dto.razza())
                .sesso(dto.sesso())
                .peso(dto.peso())
                .microchip(dto.microchip())
                .note(dto.note())
                .dataNascita(LocalDate.parse(dto.dataNascita()))
                .isDeleted(false)
                .utente(utente)
                .build();
    }
}