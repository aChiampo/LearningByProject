package com.WW.controllers;

import com.WW.entities.Azienda;
import com.WW.services.AziendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aziende")
@CrossOrigin(origins = "*") //Da cambiare in produzione per limitare l'accesso solo al dominio del frontend
@RequiredArgsConstructor
//TODO: Aggiungere le validazioni (neccessario il DTO)
public class AziendaController {

    private final AziendaService aziendaService;

    // 1. LEGGI TUTTE LE AZIENDE -> GET http://localhost:8080/api/aziende/leggiTutti
    @GetMapping("/leggiTutti")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<List<Azienda>> leggiTutti() {
        return ResponseEntity.ok(aziendaService.ottieniTutte());
    }

    // 2. LEGGI SINGOLA AZIENDA -> GET http://localhost:8080/api/aziende/leggi/12
    @GetMapping("/leggi/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Azienda> leggi(@PathVariable Integer id) {
        return ResponseEntity.ok(aziendaService.ottieniPerId(id));
    }

    // 3. CREA UNA NUOVA AZIENDA -> POST http://localhost:8080/api/aziende/crea
    @PostMapping("/crea")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Azienda> crea(@RequestBody Azienda azienda) {
        Azienda nuovaAzienda = aziendaService.creaAzienda(azienda);
        return new ResponseEntity<>(nuovaAzienda, HttpStatus.CREATED);
    }

    // 4. MODIFICA UN'AZIENDA -> PATCH http://localhost:8080/api/aziende/modifica/12
    @PatchMapping("/modifica/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Azienda> modifica(@PathVariable Integer id, @RequestBody Azienda azienda) {
        Azienda aziendaAggiornata = aziendaService.aggiornaAzienda(id, azienda);
        return ResponseEntity.ok(aziendaAggiornata);
    }

    // 5. ELIMINA UN'AZIENDA -> DELETE http://localhost:8080/api/aziende/elimina/12
    @DeleteMapping("/elimina/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> elimina(@PathVariable Integer id) {
        aziendaService.eliminaAzienda(id);
        return ResponseEntity.noContent().build();
    }
}
