package com.WW.repositories;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.WW.entities.Utente;

public interface UtenteRepo extends JpaRepository<Utente, Integer> {
    List<Utente> findByIsDeletedFalse();
    List<Utente> findByRuoloRuoloAndIsDeletedFalse(String ruolo);
    Optional<Utente> findByIdAndIsDeletedFalse(Integer id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM Utente u WHERE u.id = :id AND u.isDeleted = false")
    Optional<Utente> lockByIdAndIsDeletedFalse(@Param("id") Integer id);

    List<Utente> findByNome(String nome);
    List<Utente> findByNomeAndIsDeletedFalse(String nome);
    List<Utente> findByCognome(String cognome);
    List<Utente> findByCognomeAndIsDeletedFalse(String cognome);
    Optional<Utente> findByEmail(String email);
    Optional<Utente> findByEmailAndIsDeletedFalse(String email);
}
