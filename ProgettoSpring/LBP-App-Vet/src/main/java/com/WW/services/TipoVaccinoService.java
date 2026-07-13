package com.WW.services;

import com.WW.entities.TipoVaccino;
import com.WW.repositories.TipoVaccinoRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoVaccinoService {

    private final TipoVaccinoRepo tipoVaccinoRepo;

    public TipoVaccinoService(TipoVaccinoRepo tipoVaccinoRepo) {
        this.tipoVaccinoRepo = tipoVaccinoRepo;
    }

    /**
     * Visualizza tutti i tipi di vaccino
     * @return lista di tutti i tipi di vaccino
     */
    public List<TipoVaccino> visualizzaTuttiTipiVaccino() {
        return tipoVaccinoRepo.findByIsDeletedFalse();
    }

    /**
     * Ricerca tipi di vaccino per tipologia
     * @param tipologia la tipologia del vaccino
     * @return lista di tipi di vaccino corrispondenti
     * @throws IllegalArgumentException se la tipologia è null o vuota
     */
    public List<TipoVaccino> findByTipologia(String tipologia) {
        if (tipologia == null || tipologia.trim().isEmpty()) {
            throw new IllegalArgumentException("La tipologia del vaccino non può essere null o vuota");
        }
        return tipoVaccinoRepo.findByTipologiaAndIsDeletedFalse(tipologia);
    }

    /**
     * Ricerca tipi di vaccino per durata
     * @param durata la durata del vaccino in mesi
     * @return lista di tipi di vaccino con la durata specificata
     * @throws IllegalArgumentException se la durata è negativa o zero
     */
    public List<TipoVaccino> findByDurata(int durata) {
        if (durata <= 0) {
            throw new IllegalArgumentException("La durata del vaccino deve essere maggiore di zero");
        }
        return tipoVaccinoRepo.findByDurataAndIsDeletedFalse(durata);
    }

    /**
     * Recupera un tipo di vaccino per ID
     * @param id l'ID del tipo di vaccino
     * @return il tipo di vaccino se presente
     * @throws IllegalArgumentException se l'ID è null o negativo
     */
    public Optional<TipoVaccino> getTipoVaccinoById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID del tipo di vaccino non può essere null o negativo");
        }
        return tipoVaccinoRepo.findByIdAndIsDeletedFalse(id);
    }

    /**
     * Salva un nuovo tipo di vaccino
     * @param tipoVaccino il tipo di vaccino da salvare
     * @return il tipo di vaccino salvato
     * @throws IllegalArgumentException se i dati obbligatori sono mancanti
     */
    public TipoVaccino salvaTipoVaccino(TipoVaccino tipoVaccino) {
        if (tipoVaccino == null) {
            throw new IllegalArgumentException("Il tipo di vaccino non può essere null");
        }
        if (tipoVaccino.getTipologia() == null || tipoVaccino.getTipologia().trim().isEmpty()) {
            throw new IllegalArgumentException("La tipologia del vaccino è obbligatoria");
        }
        if (tipoVaccino.getDurata() <= 0) {
            throw new IllegalArgumentException("La durata del vaccino deve essere maggiore di zero");
        }
        if (tipoVaccino.getIsDeleted() == null) {
            tipoVaccino.setIsDeleted(false);
        }
        return tipoVaccinoRepo.save(tipoVaccino);
    }

    /**
     * Aggiorna un tipo di vaccino esistente
     * @param tipoVaccino il tipo di vaccino da aggiornare
     * @return il tipo di vaccino aggiornato
     * @throws IllegalArgumentException se i dati sono non validi o il tipo di vaccino non esiste
     */
    public TipoVaccino aggiornaTipoVaccino(TipoVaccino tipoVaccino) {
        if (tipoVaccino == null) {
            throw new IllegalArgumentException("Il tipo di vaccino non può essere null");
        }
        if (tipoVaccino.getId() == null || tipoVaccino.getId() <= 0) {
            throw new IllegalArgumentException("L'ID del tipo di vaccino è obbligatorio per l'aggiornamento");
        }
        if (tipoVaccinoRepo.findByIdAndIsDeletedFalse(tipoVaccino.getId()).isEmpty()) {
            throw new IllegalArgumentException("Tipo di vaccino con ID " + tipoVaccino.getId() + " non trovato");
        }
        if (tipoVaccino.getTipologia() == null || tipoVaccino.getTipologia().trim().isEmpty()) {
            throw new IllegalArgumentException("La tipologia del vaccino è obbligatoria");
        }
        if (tipoVaccino.getDurata() <= 0) {
            throw new IllegalArgumentException("La durata del vaccino deve essere maggiore di zero");
        }
        tipoVaccino.setIsDeleted(false);
        return tipoVaccinoRepo.save(tipoVaccino);
    }

    /**
     * Elimina un tipo di vaccino
     * @param id l'ID del tipo di vaccino da eliminare
     * @throws IllegalArgumentException se l'ID è null, negativo o il tipo di vaccino non esiste
     */
    public void eliminaTipoVaccino(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID del tipo di vaccino non può essere null o negativo");
        }
        Optional<TipoVaccino> tipoVaccino = tipoVaccinoRepo.findByIdAndIsDeletedFalse(id);
        if (tipoVaccino.isEmpty()) {
            throw new IllegalArgumentException("Tipo di vaccino con ID " + id + " non trovato");
        }
        TipoVaccino tipoVaccinoToDelete = tipoVaccino.get();
        tipoVaccinoToDelete.setIsDeleted(true);
        tipoVaccinoRepo.save(tipoVaccinoToDelete);
    }
}
