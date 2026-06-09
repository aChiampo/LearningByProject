package com.spring.bar.services;

import java.util.List;

import com.spring.bar.entities.ProdottoBar;

public interface ProdottoBarService {
    ProdottoBar getProdottoBarById(Long id);
    List<ProdottoBar> getAllProdottiBar();
    List<ProdottoBar> getProdottiBarBySezione(String sezione);

    List<String> getSezioni();

    ProdottoBar addProdottoBar(ProdottoBar prodottoBar);
    ProdottoBar updateProdottoBar(Long id, ProdottoBar prodottoBar);
    void deleteProdottoBar(Long id);
}
