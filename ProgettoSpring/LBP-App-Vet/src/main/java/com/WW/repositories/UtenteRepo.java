package com.WW.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WW.entities.Utente;

public interface UtenteRepo extends JpaRepository<Utente, Integer> {

    List<Utente> findByNome(String nome);
    List<Utente> findByCognome(String cognome);
    List<Utente> findByEmail(String email);
}
