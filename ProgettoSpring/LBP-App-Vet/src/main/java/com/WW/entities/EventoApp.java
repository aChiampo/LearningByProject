package com.WW.entities;

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

/**
 * @author: cristian.pappalardo
 *          Modello di entità per la tabella EventiApp
 *          Last update: 26/06/2026
 */
@Entity
@Table(name = "eventi_app")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventoApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente", nullable = false)
    private Utente utente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ruolo", nullable = false)
    private Ruolo ruolo;

    @Column(name = "tipo_evento", nullable = false, length = 20)
    private String tipoEvento;

    @Column(name = "id_oggetto")
    private Integer idOggetto;

    @Column(name = "oggetto_azione", length = 30)
    private String oggettoAzione;

    @Column(name = "time_stamp")
    private LocalDateTime timeStamp;

    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

}
