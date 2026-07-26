package com.WW.repositories;

import com.WW.entities.TipoVaccino;
import com.WW.entities.Vaccinazione;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VaccinazioneRepo extends JpaRepository<Vaccinazione, Integer> {
    List<Vaccinazione> findByIsDeletedFalse();
    Optional<Vaccinazione> findByIdAndIsDeletedFalse(Integer id);

    List<Vaccinazione> findByIdTipoVaccino(TipoVaccino idTipoVaccino);
    List<Vaccinazione> findByIdTipoVaccinoAndIsDeletedFalse(TipoVaccino idTipoVaccino);
    List<Vaccinazione> findByIdAnimaleIdAndIsDeletedFalse(Integer idAnimale);

    List<Vaccinazione> findByDataVaccinazione(LocalDateTime dataVaccinazione);
    List<Vaccinazione> findByDataVaccinazioneAndIsDeletedFalse(LocalDateTime dataVaccinazione);

    List<Vaccinazione> findByLotto(String lotto);
    List<Vaccinazione> findByLottoAndIsDeletedFalse(String lotto);
}
