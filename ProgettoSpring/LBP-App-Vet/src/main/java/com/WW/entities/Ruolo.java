package com.WW.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Modello di entità per la tabella RUOLI.
 */
@Entity
@Table(name = "ruoli")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ruolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ruolo", length = 15, nullable = false, unique = true)
    private String ruolo;
}