package com.WW.entities;

import java.math.BigDecimal;
import java.util.List;

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

/**
 * @author: A. Chiampo
 * Modello di entità per la tabella TIPI_VISITE
 * Last update: 26/06/2026
 */
@Data
@Entity
@Table(name = "TIPI_VISITE")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TipoVisita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "Nome", length = 80, nullable = false)
    private String nome;

    @Column(name = "Durata", nullable = false)
    private int durata;

    @ManyToOne
    @JoinColumn(name = "id_categoria", referencedColumnName= "ID", nullable= true)
    private CategoriaVisite categoria;
    
    @Column(name = "Prezzo",precision = 8, scale = 2, nullable = false)
    private BigDecimal prezzo;
    
    @ManyToOne
    @JoinColumn(name = "id_dottore", referencedColumnName= "ID", nullable=false)
    private Utente dottore;
    
    @Column(name = "isDeleted", nullable = false)
    private Boolean isDeleted;

    //relazione 1-n con VISITA - cristian.pappalardo
    @OneToMany(mappedBy = "tipoVisita", cascade = CascadeType.ALL)
    private List<Visita> visita;

    @Builder.Default
    @Column(name = "Attivo", nullable = false)
    private boolean attivo=true;
}
