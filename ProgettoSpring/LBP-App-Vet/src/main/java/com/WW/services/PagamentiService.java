package com.WW.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.WW.entities.Pagamento;
import com.WW.repositories.PagamentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PagamentiService {

    private final PagamentoRepository pagamentoRepository;

    @Transactional
    public Pagamento creaPagamento(Pagamento pagamento) {
        pagamento.setDeleted(false);
        return pagamentoRepository.save(pagamento);
    }

    @Transactional
    public Pagamento aggiornaPagamento(Integer id, Pagamento datiAggiornati) {
        Pagamento esistente = ottieniPerId(id);

        if (datiAggiornati.getImportoTotale() != null) {
            esistente.setImportoTotale(datiAggiornati.getImportoTotale());
        }
        if (datiAggiornati.getTipoPagamento() != null && !datiAggiornati.getTipoPagamento().isBlank()) {
            esistente.setTipoPagamento(datiAggiornati.getTipoPagamento());
        }
        if (datiAggiornati.getStato() != null && !datiAggiornati.getStato().isBlank()) {
            esistente.setStato(datiAggiornati.getStato());
        }
        if (datiAggiornati.getData() != null) {
            esistente.setData(datiAggiornati.getData());
        }

        return pagamentoRepository.save(esistente);
    }

    @Transactional(readOnly = true)
    public List<Pagamento> ottieniTutti() {
        return pagamentoRepository.findByIsDeletedFalse();
    }

    @Transactional(readOnly = true)
    public List<Pagamento> ottieniPerStato(String stato) {
        return pagamentoRepository.findByTipoPagamentoAndIsDeletedFalse(stato);
    }

    @Transactional(readOnly = true)
    public Pagamento ottieniPerId(Integer id) {
        return pagamentoRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pagamento non trovato."));
    }

    @Transactional
    public void eliminaPagamento(Integer id) {
        Pagamento pagamento = pagamentoRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Impossibile eliminare: pagamento non trovato."));
        pagamento.setDeleted(true);
        pagamentoRepository.save(pagamento);
    }
}
