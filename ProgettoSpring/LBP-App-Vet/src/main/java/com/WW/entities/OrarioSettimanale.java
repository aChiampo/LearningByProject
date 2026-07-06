package com.WW.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author cristian.pappalardo
 * Modello di entità per la tabella OrarioSettimanale
 * Last update 28/08/2026
 */
@Entity
@Table(name = "orario_settimanale")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrarioSettimanale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "giorno_settimana", nullable = false, updatable = false)
    private int giornoSettimana;
    @Column(name = "mattina_inizio", nullable = false)
    private int mattinaInizio;
    @Column(name = "mattina_fine", nullable = false)
    private int mattinaFine;
    @Column(name = "pomeriggio_inizio", nullable = false)
    private int pomeriggioInizio;
    @Column(name = "pomeriggio_fine", nullable = false)
    private int pomeriggioFine;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente", nullable = false)
    private Utente utente;
}
