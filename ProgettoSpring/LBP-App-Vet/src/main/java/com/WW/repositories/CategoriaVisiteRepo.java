package com.WW.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WW.entities.CategoriaVisite;

public interface CategoriaVisiteRepo extends JpaRepository<CategoriaVisite, Integer> {
    List<CategoriaVisite> findByIsDeletedFalse();
    Optional<CategoriaVisite> findByIdAndIsDeletedFalse(Integer id);

    List<CategoriaVisite> findByNome(String nome);
    List<CategoriaVisite> findByNomeAndIsDeletedFalse(String nome);
}
