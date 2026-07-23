package com.WW.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.WW.dto.input.CreateAnimaleDTO;
import com.WW.dto.output.AnimaleOutputDTO;
import com.WW.entities.Animale;
import com.WW.entities.Utente;
import com.WW.repositories.AnimaleRepository;
import com.WW.repositories.UtenteRepository;

@Service
public class AnimaleService {

    private final AnimaleRepository animaleRepository;
    private final UtenteRepository utenteRepository;

    public AnimaleService(AnimaleRepository animaleRepository, UtenteRepository utenteRepository) {
        this.animaleRepository = animaleRepository;
        this.utenteRepository = utenteRepository;
    }

    // 1. REGISTRA UN NUOVO ANIMALE
    @Transactional
    public AnimaleOutputDTO creaAnimale(CreateAnimaleDTO animale, Utente utente) {
        // Controllo unicità del microchip
        if (animale.microchip() != null && !animale.microchip().isBlank()) {
            animaleRepository.findByMicrochipAndIsDeletedFalse(animale.microchip())
                    .ifPresent(a -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Il microchip " + a.getMicrochip() + " risulta già registrato nel sistema.");
                    });
        }

        // Controllo che l'utente associato all'animale esista
        Utente utente = utenteRepository.findByIdAndIsDeletedFalse(animale.utenteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Utente con ID " + animale.utenteId() + " non trovato."));

        // Converte il DTO in entità e salva l'animale
        Animale savedAnimale = animaleRepository.save(Animale.fromDTO(animale, utente));

        // Converte l'entità salvata in DTO e restituisce il risultato
        AnimaleOutputDTO animaleOutputDTO = AnimaleOutputDTO.fromEntity(savedAnimale);

        return animaleOutputDTO;
    }

    // 2. AGGIORNA UN ANIMALE ESISTENTE
    @Transactional
    public Animale aggiornaAnimale(Integer id, Animale datiAggiornati) {
        Animale esistente = ottieniPerId(id);

        // Controllo unicità solo se il microchip viene effettivamente cambiato
        if (datiAggiornati.getMicrochip() != null && !datiAggiornati.getMicrochip().isBlank()) {
            animaleRepository.findByMicrochipAndIsDeletedFalse(datiAggiornati.getMicrochip())
                    .filter(a -> !a.getId().equals(id))
                    .ifPresent(a -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Il microchip " + a.getMicrochip() + " risulta già registrato nel sistema.");
                    });
            esistente.setMicrochip(datiAggiornati.getMicrochip());
        }

        // Se i campi sono null o vuoti, rimangono invariati, altrimenti vengono
        // aggiornati
        if (datiAggiornati.getNome() != null && !datiAggiornati.getNome().isBlank()) {
            esistente.setNome(datiAggiornati.getNome());
        }
        if (datiAggiornati.getSpecie() != null) {
            esistente.setSpecie(datiAggiornati.getSpecie());
        }
        if (datiAggiornati.getRazza() != null) {
            esistente.setRazza(datiAggiornati.getRazza());
        }
        if (datiAggiornati.getSesso() != null) {
            esistente.setSesso(datiAggiornati.getSesso());
        }
        if (datiAggiornati.getDataNascita() != null) {
            esistente.setDataNascita(datiAggiornati.getDataNascita());
        }
        if (datiAggiornati.getPeso() != null) {
            esistente.setPeso(datiAggiornati.getPeso());
        }
        if (datiAggiornati.getNote() != null) {
            esistente.setNote(datiAggiornati.getNote());
        }

        return animaleRepository.save(esistente);
    }

    // 3. RECUPERA TUTTI I PAZIENTI/ANIMALISTI DEL SISTEMA
    @Transactional(readOnly = true)
    public List<Animale> ottieniTutti() {
        return animaleRepository.findByIsDeletedFalse();
    }

    // 4. RECUPERA GLI ANIMALI DI UN SINGOLO UTENTE
    @Transactional(readOnly = true)
    public List<Animale> ottieniPerUtente(Integer utenteId) {
        return animaleRepository.findByUtenteIdAndIsDeletedFalse(utenteId);
    }

    // 5. RECUPERA SCHEDA SINGOLO ANIMALE
    @Transactional(readOnly = true)
    public Animale ottieniPerId(Integer id) {
        return animaleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Animale non trovato."));
    }

    // 6. CANCELLA UN ANIMALE
    @Transactional
    public void eliminaAnimale(Integer id) {
        Animale animale = animaleRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Impossibile eliminare: Animale non trovato."));
        animale.setIsDeleted(true);
        animaleRepository.save(animale);
    }
}
