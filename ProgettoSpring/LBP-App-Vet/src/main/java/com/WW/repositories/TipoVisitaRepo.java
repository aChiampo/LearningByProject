package com.WW.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WW.entities.CategoriaVisite;
import com.WW.entities.TipoVisita;
import com.WW.entities.Utente;

public interface TipoVisitaRepo extends JpaRepository<TipoVisita, Integer> {
    List<TipoVisita> findByIsDeletedFalse();
    Optional<TipoVisita> findByIdAndIsDeletedFalse(Integer id);

    List<TipoVisita> findByNome(String nome);
    List<TipoVisita> findByNomeAndIsDeletedFalse(String nome);
    List<TipoVisita> findByCategoria(CategoriaVisite categoria);
    List<TipoVisita> findByCategoriaAndIsDeletedFalse(CategoriaVisite categoria);
    List<TipoVisita> findByDottore(Utente dottore);
    List<TipoVisita> findByDottoreAndIsDeletedFalse(Utente dottore);
    List<TipoVisita> findByAttivo(boolean attivo);
    List<TipoVisita> findByAttivoAndIsDeletedFalse(boolean attivo);
}
