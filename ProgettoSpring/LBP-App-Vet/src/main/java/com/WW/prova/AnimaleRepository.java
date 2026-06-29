package com.WW.repository;

import com.WW.entities.Animale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface AnimaleRepository extends JpaRepository<Animale, Integer> {
    //query "SELECT * FROM animali WHERE microchip = ?"
    Optional<Animale> findByMicrochip(String microchip);
    //query "SELECT * FROM animali WHERE id_utente = ?"
    List<Animale> findByUtenteId(Integer utenteId);
    //TODO: Implementare metodi per ricerche piu precise attraverso piu campi
}