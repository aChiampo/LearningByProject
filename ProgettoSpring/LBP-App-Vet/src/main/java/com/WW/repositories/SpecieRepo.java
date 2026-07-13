package com.WW.repositories;


import com.WW.entities.Specie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpecieRepo extends JpaRepository<Specie, Integer> {
    List<Specie> findByIsDeletedFalse();
    Optional<Specie> findByIdAndIsDeletedFalse(Integer id);

    List<Specie> findByNome(String Nome);
    List<Specie> findByNomeAndIsDeletedFalse(String Nome);
}
