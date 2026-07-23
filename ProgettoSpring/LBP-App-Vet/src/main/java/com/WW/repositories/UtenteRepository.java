package com.WW.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WW.entities.Utente;

public interface UtenteRepository extends JpaRepository<Utente, Integer> {
    List<Utente> findByIsDeletedFalse();
    Optional<Utente> findByIdAndIsDeletedFalse(Integer id);

    List<Utente> findByNome(String nome);
    List<Utente> findByNomeAndIsDeletedFalse(String nome);
    List<Utente> findByCognome(String cognome);
    List<Utente> findByCognomeAndIsDeletedFalse(String cognome);
    Optional<Utente> findByEmail(String email);
    Optional<Utente> findByEmailAndIsDeletedFalse(String email);
}
