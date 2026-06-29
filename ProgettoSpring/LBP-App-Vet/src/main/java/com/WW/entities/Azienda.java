package com.WW.entities;

import jakarta.persistence.*;
import lombok.*;

/** 
 * @author: Anqi Xu
 * Modello di entità per la tabella AZIENDE
 * Last update: 27/06/2026
 */

@Entity
@Table(name = "AZIENDE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Azienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "RAGIONE_SOCIALE", length = 100, nullable = false)
    private String ragioneSociale;

    @Column(name = "PARTITA_IVA", length = 11, nullable = false, unique = true)
    private String partitaIva;

    @Column(name = "FORMA_GIURIDICA", length = 100, nullable = false)
    private String formaGiuridica;
}
