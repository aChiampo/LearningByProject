package com.WW.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WW.entities.CategoriaVisite;

public interface CategoriaVisiteRepo extends JpaRepository<CategoriaVisite, Integer> {

    List<CategoriaVisite> findByNome(String nome);
}
