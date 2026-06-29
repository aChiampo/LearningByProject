package com.WW.repositories;

import com.WW.entities.TipiVaccino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipiVaccinoRepo extends JpaRepository<TipiVaccino, Integer> {

    List<TipiVaccino> findByTipologia(String tipologia);

    List<TipiVaccino> findByDurata(int durata);
}
