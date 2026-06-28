package com.WW.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.WW.entities.CategoriaVisite;
import com.WW.services.CategoriaVisiteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categorieVisite")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CategoriaVisiteController {

    private final CategoriaVisiteService categoriaVisiteService;

    @GetMapping("/ottieniTutte")
    public ResponseEntity<List<CategoriaVisite>> ottieniTutte() {
        return ResponseEntity.ok(categoriaVisiteService.ottieniTutte());
    }

    @GetMapping("/ottieni/{id}")
    public ResponseEntity<CategoriaVisite> ottieni(@PathVariable Integer id) {
        return ResponseEntity.ok(categoriaVisiteService.ottieniPerId(id));
    }

    @PostMapping("/aggiungi")
    public ResponseEntity<CategoriaVisite> aggiungi(@RequestBody CategoriaVisite categoriaVisite) {
        CategoriaVisite nuovaCategoriaVisite = categoriaVisiteService.aggiungiCategoriaVisite(categoriaVisite);
        return new ResponseEntity<>(nuovaCategoriaVisite, HttpStatus.CREATED);
    }

    @PatchMapping("/modifica/{id}")
    public ResponseEntity<CategoriaVisite> modifica(
            @PathVariable Integer id,
            @RequestBody CategoriaVisite categoriaVisite) {
        CategoriaVisite categoriaVisiteAggiornata = categoriaVisiteService.modificaCategoriaVisite(id, categoriaVisite);
        return ResponseEntity.ok(categoriaVisiteAggiornata);
    }

    @DeleteMapping("/elimina/{id}")
    public ResponseEntity<Void> elimina(@PathVariable Integer id) {
        categoriaVisiteService.eliminaCategoriaVisite(id);
        return ResponseEntity.noContent().build();
    }
}
