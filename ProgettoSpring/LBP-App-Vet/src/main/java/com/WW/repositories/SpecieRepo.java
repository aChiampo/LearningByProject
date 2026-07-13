package com.WW.repositories;


import com.WW.entities.Specie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecieRepo extends JpaRepository<Specie, Integer> {

    List<Specie> findByNome(String Nome);
}
