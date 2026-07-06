package com.WW.entities;

import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author: cristian.pappalardo
 * Modello di entità per la tabella VISITA
 * Last update: 28/06/2026
 */
@Entity
@Table(name = "VISITA")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Visita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "Data_Visita", nullable = false, updatable = false)
    private LocalDateTime dataVisita;
    @ManyToOne
    @JoinColumn(name = "ID_TIPO_VISITA", nullable = false)
    private TipoVisite tipoVisita;

    @ManyToOne
    @JoinColumn(name = "ID_ANIMALE", nullable = false)
    private Animale animale;

    @ManyToOne
    @JoinColumn(name = "ID_VETERINARIO", nullable = false)
    private Utente veterinario;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_PAGAMENTO", nullable = true)
    private Pagamento pagamento;
    
    @Column(name = "Note", nullable = true)
    private String note;

}
