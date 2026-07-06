package com.WW.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Specie")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Specie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "Nome", nullable = false)
    private String Nome;
    @Column(name = "Is_Deleted", nullable = false)
    private boolean isDeleted;
}
