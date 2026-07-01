package com.WW.services;

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

        if (datiAggiornati.getImporto() != null) {
            esistente.setImporto(datiAggiornati.getImporto());
        }
        if (datiAggiornati.getMetodoPagamento() != null && !datiAggiornati.getMetodoPagamento().isBlank()) {
            esistente.setMetodoPagamento(datiAggiornati.getMetodoPagamento());
        }
        if (datiAggiornati.getStato() != null && !datiAggiornati.getStato().isBlank()) {
            esistente.setStato(datiAggiornati.getStato());
        }
        if (datiAggiornati.getDataPagamento() != null) {
            esistente.setDataPagamento(datiAggiornati.getDataPagamento());
        }

        return pagamentoRepository.save(esistente);
    }

    @Transactional(readOnly = true)
    public List<Pagamento> ottieniTutti() {
        return pagamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pagamento> ottieniPerStato(String stato) {
        return pagamentoRepository.findByStato(stato);
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
