package com.WW.repositories;

import com.WW.entities.Ruoli;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RuoliRepo extends JpaRepository<Ruoli, Integer> {

    List<Ruoli> findRuoliById(Integer id);

    List<Ruoli> findByRuolo(String Ruolo);

}
