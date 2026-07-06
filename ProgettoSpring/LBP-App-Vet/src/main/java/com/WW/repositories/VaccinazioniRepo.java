package com.WW.repositories;

import com.WW.entities.TipiVaccino;
import com.WW.entities.Vaccinazioni;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface VaccinazioniRepo extends JpaRepository<Vaccinazioni, Integer> {

    List<Vaccinazioni> findByIdTipoVaccino(TipiVaccino idTipoVaccino);

    List<Vaccinazioni> findByDataVaccinazione(LocalDateTime dataVaccinazione);

    List<Vaccinazioni> findByLotto(String lotto);
}
