package com.WW.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RUOLI")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class Razze {
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
