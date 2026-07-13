package com.WW.repositories;

import com.WW.entities.Razza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RazzaRepo extends JpaRepository<Razza, Integer> {
    List<Razza> findByIsDeletedFalse();
    Optional<Razza> findByIdAndIsDeletedFalse(Integer id);

    List<Razza> findByIdSpecie_Id(Integer specieId);
    List<Razza> findByIdSpecie_IdAndIsDeletedFalse(Integer specieId);

    List<Razza> findByNome(String Nome);
    List<Razza> findByNomeAndIsDeletedFalse(String Nome);
}
