package com.WW.entities;

  

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 

import java.time.LocalDate;
 

import org.hibernate.annotations.ManyToAny;
 

@Entity
@Table(
  name = "UTENTE",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_utente_email", columnNames = "Email")
  }
)
@Data
@AllArgsConstructor
@Builder
public class Utente {
  

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "ID")
private Integer id;
  
@Column(name = "Nome", length = 30, nullable = false)
private String nome;

@Column(name = "Cognome", length = 30, nullable = false)
private String cognome;

@Column(name = "Email", length = 100, nullable = false, unique = true)
private String email;
  
@Column(name = "Password", length = 255, nullable = false)
private String passwordHash;

@Column(name = "Telefono", length = 20, nullable = false)
private String telefono;

@Column(name = "Indirizzo", length = 30)
private String indirizzo;

@Column(name = "Citta", length = 20)
private String citta;

@Column(name = "Data_Registrazione", nullable = false)
private LocalDate dataRegistrazione;

@ManyToOne
@JoinColumn(name = "IDAzienda", referencedColumnName = "ID")
private Azienda azienda;

@Column(name = "Riferimento", length = 30)
private String riferimento;

@ManyToOne
@JoinColumn(name = "ID_Ruolo", referencedColumnName = "ID")

private Ruolo ruolo;

}