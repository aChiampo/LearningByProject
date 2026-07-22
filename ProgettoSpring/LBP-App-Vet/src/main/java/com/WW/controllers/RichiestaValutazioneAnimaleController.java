package com.WW.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        richiestaValutazioneAnimaleService.inviaRichiesta(utenteAutenticato.id(), richiesta);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new RichiestaValutazioneAnimaleResponse(
                        "INVIATA",
                        MESSAGGIO_RICHIESTA_INVIATA));
    }
}
