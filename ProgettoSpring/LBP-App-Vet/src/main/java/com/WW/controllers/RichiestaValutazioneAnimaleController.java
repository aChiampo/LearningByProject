package com.WW.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.WW.dto.RichiestaValutazioneAnimaleDettaglioDto;
import com.WW.dto.RichiestaValutazioneAnimaleDto;
import com.WW.dto.RichiestaValutazioneAnimaleResponse;
import com.WW.services.RichiestaValutazioneAnimaleService;
import com.WW.sicurezza.UtenteAutenticato;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/richieste-animali")
@RequiredArgsConstructor
public class RichiestaValutazioneAnimaleController {

    private static final String MESSAGGIO_RICHIESTA_INVIATA =
            "Richiesta inviata correttamente. Riceverai una risposta via mail.";

    private final RichiestaValutazioneAnimaleService richiestaValutazioneAnimaleService;

    @PostMapping("/valutazione")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<RichiestaValutazioneAnimaleResponse> richiediValutazione(
            @AuthenticationPrincipal UtenteAutenticato utenteAutenticato,
            @Valid @RequestBody RichiestaValutazioneAnimaleDto richiesta) {
        RichiestaValutazioneAnimaleDettaglioDto richiestaSalvata =
                richiestaValutazioneAnimaleService.inviaRichiesta(utenteAutenticato.id(), richiesta);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new RichiestaValutazioneAnimaleResponse(
                        "INVIATA",
                        MESSAGGIO_RICHIESTA_INVIATA,
                        richiestaSalvata.id()));
    }

    @GetMapping("/aperte")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<List<RichiestaValutazioneAnimaleDettaglioDto>> ottieniAperte() {
        return ResponseEntity.ok(richiestaValutazioneAnimaleService.ottieniRichiesteAperte());
    }

    @PatchMapping("/{id}/chiudi")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<Void> chiudi(@PathVariable Integer id) {
        richiestaValutazioneAnimaleService.chiudiRichiesta(id);
        return ResponseEntity.noContent().build();
    }
}
