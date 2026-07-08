package com.WW.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WW.entities.Ruolo;

public interface RuoloRepo extends JpaRepository<Ruolo, Integer> {


    Optional<Ruolo> findByRuolo(String ruolo);

}
