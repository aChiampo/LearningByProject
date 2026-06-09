package com.spring.bar.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "listino_bar")
@Data
public class ProdottoBar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sezione;
    private String prodotto;
    private Double prezzo;
    private Double kcal;

    public ProdottoBar() {
    }
}
