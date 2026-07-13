package com.WW.entities;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "codice_fiscale", length = 16, nullable = true, unique = true)
    private String codiceFiscale;

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
    @JoinColumn(name = "id_azienda", referencedColumnName = "ID", nullable = true)
    private Azienda azienda;

    @ManyToOne
    @JoinColumn(name = "id_ruolo", referencedColumnName = "ID", nullable = false)
    private Ruolo ruolo;

    @Column(name = "isDeleted", nullable = false)
    private Boolean isDeleted;

    // relazioni bi_dimensionali

    // relazione 1-n con VISITA - cristian.pappalardo
    @OneToMany(mappedBy = "veterinario", cascade = CascadeType.ALL)
    private List<Visita> visita;

    // relazione 1-n con ORARIO_SETTIMANALE - cristian.pappalardo
    @OneToMany(mappedBy = "utente")
    private List<OrarioSettimanale> orariSettimanali;

}
