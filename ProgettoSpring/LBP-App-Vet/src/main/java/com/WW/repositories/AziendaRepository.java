package com.WW.repositories;

import com.WW.entities.Azienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AziendaRepository extends JpaRepository<Azienda, Integer> {

    //query "SELECT * FROM aziende WHERE partita_iva = ?"
    Optional<Azienda> findByPartitaIva(String partitaIva);
    //TODO: Implementare metodi per ricerche piu precise attraverso piu campi
}
