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
 * @author: A. Chiampo
 * Modello di entità per la tabella CATEGORIE_VISITE
 * Last update: 26/06/2026
 */
@Entity
@Data
@Table(name= "CATEGORIE_VISITE")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoriaVisite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private int id;

    @Column(name = "Nome", length = 100, nullable = false)
    private String nome;
    
    @Column(name = "isDeleted", nullable = false)
    private Boolean isDeleted;



}
