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
@Table(name = "OrarioSettimanale")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrarioSettimanale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "GiornoSettimana", nullable = false, updatable = false)
    private int GiornoSettimana;
    @Column(name = "MattinaInizio", nullable = false)
    private int MattinaInizio;
    @Column(name = "MattinaFine", nullable = false)
    private int MattinaFine;
    @Column(name = "PomeriggioInizio", nullable = false)
    private int PomeriggioInizio;
    @Column(name = "PomeriggioFine", nullable = false)
    private int PomeriggioFine;
    @OneToOne
    @JoinColumn(name = "ID_UTENTE", nullable = false)
    private Utente Utente;
}
