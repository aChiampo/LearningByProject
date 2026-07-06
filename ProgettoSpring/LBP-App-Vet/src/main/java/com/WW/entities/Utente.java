package com.WW.entities;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/* @author: A. Chiampo
* Modello di entità per la tabella UTENTE
* Last update: 26/06/2026
*/
@Entity
@Table(name = "UTENTI")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "Nome", length = 30, nullable = false)
    private String nome;

    @Column(name = "Cognome", length = 30, nullable = false)
    private String cognome;

    @Column(name = "Email", length = 100, nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "Password", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "Telefono", length = 20, nullable = false)
    private String telefono;

    @Column(name = "Indirizzo", length = 30, nullable = true)
    private String indirizzo;

    @Column(name = "Citta", length = 20, nullable = true)
    private String citta;

    @Column(name = "Data_Registrazione", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dataRegistrazione = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "IDAzienda", referencedColumnName = "ID", nullable = true)
    private Azienda azienda;

    @Column(name = "Riferimento", length = 30)
    private String riferimento;

    @ManyToOne
    @JoinColumn(name = "ID_Ruolo", referencedColumnName = "ID", nullable = false)
    private Ruolo ruolo;

   // relazioni bi_dimensionali

    // relazione 1-n con VISITA - cristian.pappalardo
    @OneToMany(mappedBy = "utente")
    private List<Visita> visiteList;

    // relazione 1-1 con ORARIO_SETTIMANALE - cristian.pappalardo
    @OneToOne(mappedBy = "utente")
    private OrarioSettimanale orarioSettimanale;

}