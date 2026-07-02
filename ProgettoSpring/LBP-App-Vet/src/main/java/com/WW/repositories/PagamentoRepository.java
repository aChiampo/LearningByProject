package com.WW.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WW.entities.Pagamento;


public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {

    List<Pagamento> findByStato(String stato);
}
