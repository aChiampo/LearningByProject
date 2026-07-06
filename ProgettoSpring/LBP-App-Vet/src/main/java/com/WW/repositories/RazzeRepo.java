package com.WW.repositories;

import com.WW.entities.Razze;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RazzeRepo extends JpaRepository<Razze, Integer> {

    List<Razze> findRazzeById(Integer id);

    List<Razze> findByNome(String Nome);
}
