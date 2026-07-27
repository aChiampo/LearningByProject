package com.WW.repositories;

import com.WW.entities.Animale;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AnimaleRepository extends JpaRepository<Animale, Integer> {
    List<Animale> findByIsDeletedFalse();
    Optional<Animale> findByIdAndIsDeletedFalse(Integer id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Animale a WHERE a.id = :id AND a.isDeleted = false")
    Optional<Animale> lockByIdAndIsDeletedFalse(@Param("id") Integer id);
    //query "SELECT * FROM animali WHERE microchip = ?"
    Optional<Animale> findByMicrochip(String microchip);
    Optional<Animale> findByMicrochipAndIsDeletedFalse(String microchip);
    //query "SELECT * FROM animali WHERE id_utente = ?"
    List<Animale> findByUtenteId(Integer utenteId);
    List<Animale> findByUtenteIdAndIsDeletedFalse(Integer utenteId);
    //TODO: Implementare metodi per ricerche piu precise attraverso piu campi
}
