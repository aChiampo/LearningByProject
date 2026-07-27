package com.WW.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.WW.entities.Pagamento;
import com.WW.services.PagamentiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pagamenti")
@RequiredArgsConstructor
public class PagamentiController {

    private final PagamentiService pagamentiService;

    @GetMapping("/leggiTutti")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<List<Pagamento>> leggiTutti() {
        return ResponseEntity.ok(pagamentiService.ottieniTutti());
    }

    @GetMapping("/leggi/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Pagamento> leggi(@PathVariable Integer id) {
        return ResponseEntity.ok(pagamentiService.ottieniPerId(id));
    }


    @PostMapping("/crea")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Pagamento> crea(@RequestBody Pagamento pagamento) {
        Pagamento nuovoPagamento = pagamentiService.creaPagamento(pagamento);
        return new ResponseEntity<>(nuovoPagamento, HttpStatus.CREATED);
    }

    @PatchMapping("/modifica/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Pagamento> modifica(@PathVariable Integer id, @RequestBody Pagamento pagamento) {
        Pagamento aggiornato = pagamentiService.aggiornaPagamento(id, pagamento);
        return ResponseEntity.ok(aggiornato);
    }

    @DeleteMapping("/elimina/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> elimina(@PathVariable Integer id) {
        pagamentiService.eliminaPagamento(id);
        return ResponseEntity.noContent().build();
    }
}
