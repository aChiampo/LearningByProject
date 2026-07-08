package com.WW.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.WW.entities.Ruolo;
import com.WW.repositories.RuoloRepo;

@Service
public class RuoloService {

    private final RuoloRepo ruoloRepo;

    public RuoloService(RuoloRepo ruoloRepo) {
        this.ruoloRepo = ruoloRepo;
    }

    /**
     * Visualizza tutti i ruoli
     * @return lista di tutti i ruoli
     */
    public List<Ruolo> visualizzaTuttiRuoli() {
        return ruoloRepo.findAll();
    }

    public Optional<Ruolo> ottieniPerRuolo(String ruolo) {
        if (ruolo == null || ruolo.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del ruolo non può essere null o vuoto");
        }
        return ruoloRepo.findByRuolo(ruolo.trim().toUpperCase());
    }

    /**
     * Salva un nuovo ruolo
     * @param ruolo il ruolo da salvare
     * @return il ruolo salvato
     * @throws IllegalArgumentException se i dati obbligatori sono mancanti
     */
    public Ruolo salvaRuolo(Ruolo ruolo) {
        if (ruolo == null) {
            throw new IllegalArgumentException("Il ruolo non può essere null");
        }
        if (ruolo.getRuolo() == null || ruolo.getRuolo().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del ruolo è obbligatorio");
        }
        
        // Verifica unicità del ruolo
        ruolo.setRuolo(ruolo.getRuolo().trim().toUpperCase());
        
        if (ruoloRepo.findByRuolo(ruolo.getRuolo()).isPresent()) {
            throw new IllegalArgumentException("Un ruolo con nome '" + ruolo.getRuolo() + "' esiste già");
        }

        return ruoloRepo.save(ruolo);
    }

    /**
     * Aggiorna un ruolo esistente
     * @param ruolo il ruolo da aggiornare
     * @return il ruolo aggiornato
     * @throws IllegalArgumentException se i dati sono non validi o il ruolo non esiste
     */
    public Ruolo aggiornaRuolo(Ruolo ruolo) {
        if (ruolo == null) {
            throw new IllegalArgumentException("Il ruolo non può essere null");
        }
        if (ruolo.getId() == null || ruolo.getId() <= 0) {
            throw new IllegalArgumentException("L'ID del ruolo è obbligatorio per l'aggiornamento");
        }
        if (!ruoloRepo.existsById(ruolo.getId())) {
            throw new IllegalArgumentException("Ruolo con ID " + ruolo.getId() + " non trovato");
        }
        if (ruolo.getRuolo() == null || ruolo.getRuolo().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del ruolo è obbligatorio");
        }
        ruolo.setRuolo(ruolo.getRuolo().trim().toUpperCase());
        // Verifica unicità del ruolo (escludendo l'ID corrente)
        Optional<Ruolo> optionalRuolo = ruoloRepo.findByRuolo(ruolo.getRuolo());
        if (optionalRuolo.isPresent() && !optionalRuolo.get().getId().equals(ruolo.getId())) {
            throw new IllegalArgumentException("Un ruolo con nome '" + ruolo.getRuolo() + "' esiste già");
        }

        return ruoloRepo.save(ruolo);
    }

    /**
     * Elimina un ruolo
     * @param id l'ID del ruolo da eliminare
     * @throws IllegalArgumentException se l'ID è null, negativo o il ruolo non esiste
     */
    public void eliminaRuolo(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID del ruolo non può essere null o negativo");
        }
        if (!ruoloRepo.existsById(id)) {
            throw new IllegalArgumentException("Ruolo con ID " + id + " non trovato");
        }
        ruoloRepo.deleteById(id);
    }

    public Ruolo ottieniPerId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID del ruolo non può essere null o negativo");
        }
        return ruoloRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Ruolo con ID " + id + " non trovato"));
    }
}
