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
        if (datiAggiornati.getData() != null) {
            esistente.setData(datiAggiornati.getData());
        }

        return pagamentoRepository.save(esistente);
    }

    @Transactional(readOnly = true)
    public List<Pagamento> ottieniTutti() {
        return pagamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pagamento> ottieniPerStato(String stato) {
        return pagamentoRepository.findByTipoPagamento(stato);
    }

    @Transactional(readOnly = true)
    public Pagamento ottieniPerId(Integer id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pagamento non trovato."));
    }

    @Transactional
    public void eliminaPagamento(Integer id) {
        if (!pagamentoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossibile eliminare: pagamento non trovato.");
        }
        pagamentoRepository.deleteById(id);
    }
}
