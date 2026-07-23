package com.WW.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WW.entities.RichiestaValutazioneAnimale;

public interface RichiestaValutazioneAnimaleRepository extends JpaRepository<RichiestaValutazioneAnimale, Integer> {
    List<RichiestaValutazioneAnimale> findByIsDeletedFalseOrderByDataRichiestaDesc();
    Optional<RichiestaValutazioneAnimale> findByIdAndIsDeletedFalse(Integer id);
}
