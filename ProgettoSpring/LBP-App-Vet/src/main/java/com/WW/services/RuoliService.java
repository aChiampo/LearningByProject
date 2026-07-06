package com.WW.services;

import com.WW.entities.Ruoli;
import com.WW.repositories.RuoliRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RuoliService {

    private final RuoliRepo ruoliRepo;

    public RuoliService(RuoliRepo ruoliRepo) {
        this.ruoliRepo = ruoliRepo;
    }

    /**
     * Visualizza tutti i ruoli
     * @return lista di tutti i ruoli
     */
    public List<Ruoli> visualizzaTuttiRuoli() {
        return ruoliRepo.findAll();
    }

    /**
     * Ricerca ruoli per ID
     * @param id l'ID del ruolo
     * @return lista di ruoli corrispondenti
     * @throws IllegalArgumentException se l'ID è null o negativo
     */
    public List<Ruoli> findRuoliById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID del ruolo non può essere null o negativo");
        }
        return ruoliRepo.findRuoliById(id);
    }

    /**
     * Ricerca ruoli per nome
     * @param ruolo il nome del ruolo
     * @return lista di ruoli corrispondenti
     * @throws IllegalArgumentException se il nome del ruolo è null o vuoto
     */
    public List<Ruoli> findByRuolo(String ruolo) {
        if (ruolo == null || ruolo.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del ruolo non può essere null o vuoto");
        }
        return ruoliRepo.findByRuolo(ruolo);
    }

    /**
     * Recupera un ruolo per ID
     * @param id l'ID del ruolo
     * @return il ruolo se presente
     * @throws IllegalArgumentException se l'ID è null o negativo
     */
    public Optional<Ruoli> getRuoloById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID del ruolo non può essere null o negativo");
        }
        return ruoliRepo.findById(id);
    }

    /**
     * Salva un nuovo ruolo
     * @param ruolo il ruolo da salvare
     * @return il ruolo salvato
     * @throws IllegalArgumentException se i dati obbligatori sono mancanti
     */
    public Ruoli salvaRuolo(Ruoli ruolo) {
        if (ruolo == null) {
            throw new IllegalArgumentException("Il ruolo non può essere null");
        }
        if (ruolo.getRuolo() == null || ruolo.getRuolo().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del ruolo è obbligatorio");
        }

        // Verifica unicità del ruolo
        List<Ruoli> ruoliEsistenti = ruoliRepo.findByRuolo(ruolo.getRuolo().trim());
        if (!ruoliEsistenti.isEmpty()) {
            throw new IllegalArgumentException("Un ruolo con nome '" + ruolo.getRuolo() + "' esiste già");
        }

        return ruoliRepo.save(ruolo);
    }

    /**
     * Aggiorna un ruolo esistente
     * @param ruolo il ruolo da aggiornare
     * @return il ruolo aggiornato
     * @throws IllegalArgumentException se i dati sono non validi o il ruolo non esiste
     */
    public Ruoli aggiornaRuolo(Ruoli ruolo) {
        if (ruolo == null) {
            throw new IllegalArgumentException("Il ruolo non può essere null");
        }
        if (ruolo.getId() == null || ruolo.getId() <= 0) {
            throw new IllegalArgumentException("L'ID del ruolo è obbligatorio per l'aggiornamento");
        }
        if (!ruoliRepo.existsById(ruolo.getId())) {
            throw new IllegalArgumentException("Ruolo con ID " + ruolo.getId() + " non trovato");
        }
        if (ruolo.getRuolo() == null || ruolo.getRuolo().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del ruolo è obbligatorio");
        }

        // Verifica unicità del ruolo (escludendo l'ID corrente)
        List<Ruoli> ruoliEsistenti = ruoliRepo.findByRuolo(ruolo.getRuolo().trim());
        if (!ruoliEsistenti.isEmpty() && !ruoliEsistenti.get(0).getId().equals(ruolo.getId())) {
            throw new IllegalArgumentException("Un ruolo con nome '" + ruolo.getRuolo() + "' esiste già");
        }

        return ruoliRepo.save(ruolo);
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
        if (!ruoliRepo.existsById(id)) {
            throw new IllegalArgumentException("Ruolo con ID " + id + " non trovato");
        }
        ruoliRepo.deleteById(id);
    }
}
