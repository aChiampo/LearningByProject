package com.WW.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "PAGAMENTI")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "Data", nullable = false)
    private LocalDate data;

    @Column(name = "Tipo_Pagamento", length = 30)
    private String tipoPagamento;

    @Column(name = "Importo_Totale", precision = 8, scale = 2, nullable = false)
    private BigDecimal importoTotale;

    @ManyToOne
    @JoinColumn(name = "ID_Utente", referencedColumnName = "ID")
    private Utente utente;

    @Column(name = "RiferimentoFile", nullable = false)
    private Integer riferimentoFile;

    @Builder.Default
    @Column(name = "isDeleted", nullable = false)
    private boolean isDeleted = false;
}
