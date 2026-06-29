package com.WW.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WW.entities.CategoriaVisite;
import com.WW.entities.TipoVisite;
import com.WW.entities.Utente;

public interface TipoVisiteRepo extends JpaRepository<TipoVisite, Integer> {

    List<TipoVisite> findByNome(String nome);
    List<TipoVisite> findByCategoria(CategoriaVisite categoria);
    List<TipoVisite> findByDottore(Utente dottore);
    List<TipoVisite> findByAttivo(boolean attivo);
}
