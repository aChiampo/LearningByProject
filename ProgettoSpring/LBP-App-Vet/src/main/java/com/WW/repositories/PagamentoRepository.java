package com.WW.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.WW.entities.Pagamento;
import com.WW.entities.Utente;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {

    List<Pagamento> findByTipoPagamentoAndIsDeletedFalse(String tipoPagamento);

    List<Pagamento> findByUtenteAndIsDeletedFalse(Utente utente);

    List<Pagamento> findByDataAndIsDeletedFalse(LocalDate data);

    List<Pagamento> findByImportoTotaleAndIsDeletedFalse(BigDecimal importoTotale);

    List<Pagamento> findByIsDeletedFalse();

    Optional<Pagamento> findByIdAndIsDeletedFalse(Integer id);
}
