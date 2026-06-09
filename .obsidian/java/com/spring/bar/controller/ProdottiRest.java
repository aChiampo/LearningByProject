package com.spring.bar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.bar.entities.ProdottoBar;
import com.spring.bar.services.ProdottoBarService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/prodotti")
public class ProdottiRest {
    @Autowired
    private ProdottoBarService service;

    @GetMapping("")
    public ResponseEntity<List<ProdottoBar>> prodotti() {
        return ResponseEntity.ok(service.getAllProdottiBar());
    }

    @GetMapping("/sezione/{sezione}")
    public ResponseEntity<List<ProdottoBar>> prodottiPerSezione(@PathVariable String sezione) {
        System.out.println("Sezione: " + sezione);
        return ResponseEntity.ok(service.getProdottiBarBySezione(sezione));
    }

    @GetMapping("/sezioni")
    public ResponseEntity<List<String>> getSezioni() {
        return ResponseEntity.ok(service.getSezioni());
    }

    @PostMapping("")
    public ResponseEntity<ProdottoBar> addProdotto(@RequestBody ProdottoBar p) {
        return ResponseEntity.ok(service.addProdottoBar(p));
    }
    @PostMapping("/tantiprodotti")
    public ResponseEntity<List<ProdottoBar>> addProdotti(@RequestBody List<ProdottoBar> prodotti) {
        for (ProdottoBar p : prodotti) {
            service.addProdottoBar(p);
        }
        return ResponseEntity.ok(prodotti);
    }
    
}
