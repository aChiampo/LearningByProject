package com.WW.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name = "RAZZE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Razza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "Nome", nullable = false)
    private String Nome;
    @ManyToOne
    @JoinColumn(name = "ID_SPECIE", nullable = false)//
    private Specie idSpecie;
    
    @Column(name = "Is_Deleted", nullable = false)
    private boolean isDeleted;
}
