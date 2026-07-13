package com.WW.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.WW.entities.Azienda;

@Repository
public interface AziendaRepository extends JpaRepository<Azienda, Integer> {
    List<Azienda> findByIsDeletedFalse();
    Optional<Azienda> findByIdAndIsDeletedFalse(Integer id);

    //query "SELECT * FROM aziende WHERE partita_iva = ?"
    Optional<Azienda> findByPartitaIva(String partitaIva);
    Optional<Azienda> findByPartitaIvaAndIsDeletedFalse(String partitaIva);
    //TODO: Implementare metodi per ricerche piu precise attraverso piu campi
}
