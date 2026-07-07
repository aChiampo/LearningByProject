package com.WW.repositories;

import com.WW.entities.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RuoloRepo extends JpaRepository<Ruolo, Integer> {

    List<Ruolo> findRuoliById(Integer id);

    List<Ruolo> findByRuolo(String Ruolo);

}
