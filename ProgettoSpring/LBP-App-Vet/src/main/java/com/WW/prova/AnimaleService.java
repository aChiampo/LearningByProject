package com.WW.services;

import lombok.RequiredArgsConstructor;
import com.WW.entities.Animale;
import com.WW.repository.AnimaleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnimaleService {

    private final AnimaleRepository animaleRepository;

    // 1. REGISTRA UN NUOVO ANIMALE
    @Transactional
    public Animale creaAnimale(Animale animale) {
        // Validazione dell'utente proprietario (da aggiungere quando verrà implementato
        // il repository Utente)
        /*
         * if (animale.getUtente() == null || animale.getUtente().getId() == null
         * || !utenteRepository.existsById(animale.getUtente().getId())) {
         * throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
         * "Utente proprietario non valido o inesistente.");
         * }
         */
        if (animale.getMicrochip() != null && !animale.getMicrochip().isBlank()) {
            animaleRepository.findByMicrochip(animale.getMicrochip())
                    .ifPresent(a -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Il microchip " + a.getMicrochip() + " risulta già registrato nel sistema.");
                    });
        }
        return animaleRepository.save(animale);
    }

    // 2. AGGIORNA UN ANIMALE ESISTENTE
    @Transactional
    public Animale aggiornaAnimale(Integer id, Animale datiAggiornati) {
        Animale esistente = ottieniPerId(id);

        // Controllo unicità solo se il microchip viene effettivamente cambiato
        if (datiAggiornati.getMicrochip() != null && !datiAggiornati.getMicrochip().isBlank()) {
            animaleRepository.findByMicrochip(datiAggiornati.getMicrochip())
                    .filter(a -> !a.getId().equals(id))
                    .ifPresent(a -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Il microchip " + a.getMicrochip() + " risulta già registrato nel sistema.");
                    });
            esistente.setMicrochip(datiAggiornati.getMicrochip());
        }
        
        //Se i campi sono null o vuoti, rimangono invariati, altrimenti vengono aggiornati
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
        return animaleRepository.findAll();
    }

    // 4. RECUPERA GLI ANIMALI DI UN SINGOLO UTENTE
    @Transactional(readOnly = true)
    public List<Animale> ottieniPerUtente(Integer utenteId) {
        return animaleRepository.findByUtenteId(utenteId);
    }

    // 5. RECUPERA SCHEDA SINGOLO ANIMALE
    @Transactional(readOnly = true)
    public Animale ottieniPerId(Integer id) {
        return animaleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Animale non trovato."));
    }

    // 6. CANCELLA UN ANIMALE
    @Transactional
    public void eliminaAnimale(Integer id) {
        if (!animaleRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Impossibile eliminare: Animale non trovato.");
        }
        animaleRepository.deleteById(id);
    }
}