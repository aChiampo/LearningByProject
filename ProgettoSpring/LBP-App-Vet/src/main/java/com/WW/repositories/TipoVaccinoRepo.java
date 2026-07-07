package com.WW.repositories;

import com.WW.entities.TipoVaccino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoVaccinoRepo extends JpaRepository<TipoVaccino, Integer> {

    List<TipoVaccino> findByTipologia(String tipologia);

    List<TipoVaccino> findByDurata(int durata);
}
