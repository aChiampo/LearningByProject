package com.WW.repositories;

import com.WW.entities.TipoVaccino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoVaccinoRepo extends JpaRepository<TipoVaccino, Integer> {
    List<TipoVaccino> findByIsDeletedFalse();
    Optional<TipoVaccino> findByIdAndIsDeletedFalse(Integer id);

    List<TipoVaccino> findByTipologia(String tipologia);
    List<TipoVaccino> findByTipologiaAndIsDeletedFalse(String tipologia);

    List<TipoVaccino> findByDurata(int durata);
    List<TipoVaccino> findByDurataAndIsDeletedFalse(int durata);
}
