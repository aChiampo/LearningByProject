package com.WW.entities;

import jakarta.annotation.Generated;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: cristian.pappalardo
 *          Modello di entità per la tabella EventiApp
 *          Last update: 26/06/2026
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventiApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "id_utente", nullable = false)
    private Utente utente;
    //id ruolo???

}
