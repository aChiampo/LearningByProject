package com.WW.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.WW.entities.Pagamento;


public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {
    List<Pagamento> findByIsDeletedFalse();
    Optional<Pagamento> findByIdAndIsDeletedFalse(Integer id);

    List<Pagamento> findByTipoPagamento(String tipoPagamento);
    List<Pagamento> findByTipoPagamentoAndIsDeletedFalse(String tipoPagamento);
}
