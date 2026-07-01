package com.WW.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.WW.entities.Visita;
import com.WW.repositories.AnimaleRepository;
import com.WW.repositories.TipoVisiteRepo;
import com.WW.repositories.UtenteRepo;
import com.WW.repositories.VisiteRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrenotazioniService {

    private final VisiteRepo visitaRepository;
    private final TipoVisiteRepo tipoVisiteRepo;
    private final AnimaleRepository animaleRepository;
    private final UtenteRepo utenteRepo;

    @Transactional
    public Visita creaPrenotazione(Visita visita) {
        validaRelazioni(visita);
        return visitaRepository.save(visita);
    }

    @Transactional
    public Visita aggiornaPrenotazione(Integer id, Visita datiAggiornati) {
        Visita esistente = ottieniPerId(id);

        if (datiAggiornati.getDataVisita() != null) {
            esistente.setDataVisita(datiAggiornati.getDataVisita());
        }
        if (datiAggiornati.getTipoVisita() != null) {
            validaTipoVisita(datiAggiornati.getTipoVisita().getId());
            esistente.setTipoVisita(datiAggiornati.getTipoVisita());
        }
        if (datiAggiornati.getAnimale() != null) {
            validaAnimale(datiAggiornati.getAnimale().getId());
            esistente.setAnimale(datiAggiornati.getAnimale());
        }
        if (datiAggiornati.getVeterinario() != null) {
            validaVeterinario(datiAggiornati.getVeterinario().getId());
            esistente.setVeterinario(datiAggiornati.getVeterinario());
        }
        if (datiAggiornati.getPagamento() != null) {
            esistente.setPagamento(datiAggiornati.getPagamento());
        }

        return visitaRepository.save(esistente);
    }

    @Transactional(readOnly = true)
    public List<Visita> ottieniTutte() {
        return visitaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Visita> ottieniPerAnimale(Integer animaleId) {
        return visitaRepository.findByAnimaleId(animaleId);
    }

    @Transactional(readOnly = true)
    public List<Visita> ottieniPerVeterinario(Integer veterinarioId) {
        return visitaRepository.findByVeterinarioId(veterinarioId);
    }

    @Transactional(readOnly = true)
    public Visita ottieniPerId(Integer id) {
        return visitaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Prenotazione non trovata."));
    }

    @Transactional
    public void eliminaPrenotazione(Integer id) {
        if (!visitaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossibile eliminare: prenotazione non trovata.");
        }
        visitaRepository.deleteById(id);
    }

    private void validaRelazioni(Visita visita) {
        if (visita.getTipoVisita() != null && visita.getTipoVisita().getId() != 0) {
            validaTipoVisita(visita.getTipoVisita().getId());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo visita non valido.");
        }

        if (visita.getAnimale() != null && visita.getAnimale().getId() != 0) {
            validaAnimale(visita.getAnimale().getId());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Animale non valido.");
        }

        if (visita.getVeterinario() != null && visita.getVeterinario().getId() != 0) {
            validaVeterinario(visita.getVeterinario().getId());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veterinario non valido.");
        }
    }

    private void validaTipoVisita(Integer id) {
        if (!tipoVisiteRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo visita non trovato.");
        }
    }

    private void validaAnimale(Integer id) {
        if (!animaleRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Animale non trovato.");
        }
    }

    private void validaVeterinario(Integer id) {
        if (!utenteRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Veterinario non trovato.");
        }
    }
}
