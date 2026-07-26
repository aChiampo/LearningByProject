package com.WW.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.WW.dto.RichiestaValutazioneAnimaleDettaglioDto;
import com.WW.dto.RichiestaValutazioneAnimaleDto;
import com.WW.entities.RichiestaValutazioneAnimale;
import com.WW.entities.Utente;
import com.WW.repositories.RichiestaValutazioneAnimaleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RichiestaValutazioneAnimaleService {

    private final UtenteService utenteService;
    private final RichiestaValutazioneAnimaleRepository richiestaValutazioneAnimaleRepository;

    @Transactional
    public RichiestaValutazioneAnimaleDettaglioDto inviaRichiesta(Integer utenteId, RichiestaValutazioneAnimaleDto richiesta) {
        Utente cliente = utenteService.ottieniPerId(utenteId);
        RichiestaValutazioneAnimale richiestaSalvata = richiestaValutazioneAnimaleRepository.save(toEntity(richiesta, cliente));

        return toDto(richiestaSalvata);
    }

    @Transactional(readOnly = true)
    public List<RichiestaValutazioneAnimaleDettaglioDto> ottieniRichiesteAperte() {
        return richiestaValutazioneAnimaleRepository.findByIsDeletedFalseOrderByDataRichiestaDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void chiudiRichiesta(Integer id) {
        RichiestaValutazioneAnimale richiesta = richiestaValutazioneAnimaleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Richiesta non trovata."));

        richiesta.setIsDeleted(true);
        richiestaValutazioneAnimaleRepository.save(richiesta);
    }

    private RichiestaValutazioneAnimale toEntity(RichiestaValutazioneAnimaleDto richiesta, Utente cliente) {
        return RichiestaValutazioneAnimale.builder()
                .nome(richiesta.nome())
                .specie(richiesta.specie())
                .razza(richiesta.razza())
                .sesso(richiesta.sesso())
                .dataNascita(richiesta.dataNascita())
                .peso(richiesta.peso())
                .microchip(richiesta.microchip())
                .note(richiesta.note())
                .descrizione(richiesta.descrizione())
                .utente(cliente)
                .build();
    }

    private RichiestaValutazioneAnimaleDettaglioDto toDto(RichiestaValutazioneAnimale richiesta) {
        Utente cliente = richiesta.getUtente();

        return new RichiestaValutazioneAnimaleDettaglioDto(
                richiesta.getId(),
                richiesta.getNome(),
                richiesta.getSpecie(),
                richiesta.getRazza(),
                richiesta.getSesso(),
                richiesta.getDataNascita(),
                richiesta.getPeso(),
                richiesta.getMicrochip(),
                richiesta.getNote(),
                richiesta.getDescrizione(),
                richiesta.getDataRichiesta(),
                cliente.getId(),
                nomeCompleto(cliente),
                cliente.getEmail(),
                cliente.getTelefono());
    }

    private String nomeCompleto(Utente cliente) {
        return "%s %s".formatted(cliente.getNome(), cliente.getCognome()).trim();
    }
}
