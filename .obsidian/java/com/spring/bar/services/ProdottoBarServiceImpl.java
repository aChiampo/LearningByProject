package com.spring.bar.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.bar.entities.ProdottoBar;
import com.spring.bar.repos.ProdottoBarRepo;

@Service
public class ProdottoBarServiceImpl implements ProdottoBarService {

    @Autowired
    private ProdottoBarRepo repo;
    
    @Override
    public ProdottoBar addProdottoBar(ProdottoBar prodottoBar) {
        return repo.save(prodottoBar);
    }

    @Override
    public void deleteProdottoBar(Long id) {
        repo.deleteById(id);        
    }

    @Override
    public List<ProdottoBar> getAllProdottiBar() {
        return repo.findAll();
    }

    @Override
    public List<ProdottoBar> getProdottiBarBySezione(String sezione) {
        return repo.findBySezione(sezione);
    }

    public List<String> getSezioni() {
        return this.getAllProdottiBar()
                .stream()
                .map(p -> p.getSezione())
                .distinct()
                .toList();
    }

    @Override
    public ProdottoBar getProdottoBarById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public ProdottoBar updateProdottoBar(Long id, ProdottoBar prodottoBar) {
        return repo.save(prodottoBar);
    }

}
