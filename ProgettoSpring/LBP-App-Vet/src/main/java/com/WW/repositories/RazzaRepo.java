package com.WW.repositories;

import com.WW.entities.Razza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RazzaRepo extends JpaRepository<Razza, Integer> {

    List<Razza> findByIdSpecie_Id(Integer specieId);

    List<Razza> findByNome(String Nome);
}
