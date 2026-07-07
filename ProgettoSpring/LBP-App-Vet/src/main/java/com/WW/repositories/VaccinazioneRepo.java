package com.WW.repositories;

import com.WW.entities.TipoVaccino;
import com.WW.entities.Vaccinazione;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface VaccinazioneRepo extends JpaRepository<Vaccinazione, Integer> {

    List<Vaccinazione> findByIdTipoVaccino(TipoVaccino idTipoVaccino);

    List<Vaccinazione> findByDataVaccinazione(LocalDateTime dataVaccinazione);

    List<Vaccinazione> findByLotto(String lotto);
}
