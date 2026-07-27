package com.WW.repositories;

import com.WW.entities.TipoVaccino;
import com.WW.entities.Vaccinazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
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

    // 1. Trova lo storico vaccinale di uno specifico animale tramite l'ID
    // dell'animale
    List<Vaccinazione> findByIdAnimaleIdAndIsDeletedFalseOrderByDataVaccinazioneDesc(Integer idAnimale);

    // 2. Trova le vaccinazioni in scadenza/richiamo tramite query JPQL
    // personalizzata
    // Somma i mesi di validità (durata) definiti in TipoVaccino alla
    // dataVaccinazione
    @Query("SELECT v FROM Vaccinazione v " +
            "JOIN v.idTipoVaccino t " +
            "WHERE v.isDeleted = false " +
            "AND v.dataVaccinazione IS NOT NULL " +
            "AND FUNCTION('TIMESTAMPADD', MONTH, t.durata, v.dataVaccinazione) BETWEEN :inizio AND :fine")
    List<Vaccinazione> findInScadenzaTra(@Param("inizio") LocalDateTime inizio, @Param("fine") LocalDateTime fine);
}
