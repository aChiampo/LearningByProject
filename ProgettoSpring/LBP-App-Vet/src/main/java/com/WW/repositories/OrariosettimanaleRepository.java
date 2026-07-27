package com.WW.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WW.entities.OrarioSettimanale;

/**
 * @author: cristian.pappalardo
 * Repository per la tabella OrarioSettimanale
 * Last update: 28/08/2026
 */
public interface OrariosettimanaleRepository extends JpaRepository<OrarioSettimanale, Integer> {

    Optional<OrarioSettimanale> findByUtenteIdAndGiornoSettimana(Integer utenteId, int giornoSettimana);

    List<OrarioSettimanale> findByUtenteId(Integer utenteId);
}
