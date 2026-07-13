package com.WW.services;

import com.WW.entities.Vaccinazione;
import com.WW.entities.TipoVaccino;
import com.WW.repositories.VaccinazioneRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VaccinazioneService {

    private final VaccinazioneRepo vaccinazioneRepo;

    public VaccinazioneService(VaccinazioneRepo vaccinazioneRepo) {
        this.vaccinazioneRepo = vaccinazioneRepo;
    }

    /**
     * Visualizza tutte le vaccinazioni effettuate
     * @return lista di tutte le vaccinazioni
     */
    public List<Vaccinazione> visualizzaTutteVaccinazioni() {
        return vaccinazioneRepo.findByIsDeletedFalse();
    }

    /**
     * Ricerca vaccinazioni per tipo di vaccino
     * @param idTipoVaccino il tipo di vaccino
     * @return lista di vaccinazioni corrispondenti
     * @throws IllegalArgumentException se il tipo di vaccino è null
     */
    public List<Vaccinazione> findByIdTipoVaccino(TipoVaccino idTipoVaccino) {
        if (idTipoVaccino == null) {
            throw new IllegalArgumentException("Il tipo di vaccino non può essere null");
        }
        return vaccinazioneRepo.findByIdTipoVaccinoAndIsDeletedFalse(idTipoVaccino);
    }

    /**
     * Ricerca vaccinazioni per data di vaccinazione
     * @param dataVaccinazione la data di vaccinazione
     * @return lista di vaccinazioni corrispondenti
     * @throws IllegalArgumentException se la data è null
     */
    public List<Vaccinazione> findByDataVaccinazione(LocalDateTime dataVaccinazione) {
        if (dataVaccinazione == null) {
            throw new IllegalArgumentException("La data di vaccinazione non può essere null");
        }
        return vaccinazioneRepo.findByDataVaccinazioneAndIsDeletedFalse(dataVaccinazione);
    }

    /**
     * Ricerca vaccinazioni per lotto
     * @param lotto il numero di lotto
     * @return lista di vaccinazioni corrispondenti
     * @throws IllegalArgumentException se il lotto è null o vuoto
     */
    public List<Vaccinazione> findByLotto(String lotto) {
        if (lotto == null || lotto.trim().isEmpty()) {
            throw new IllegalArgumentException("Il numero di lotto non può essere null o vuoto");
        }
        return vaccinazioneRepo.findByLottoAndIsDeletedFalse(lotto);
    }

    /**
     * Recupera una vaccinazione per ID
     * @param id l'ID della vaccinazione
     * @return la vaccinazione se presente
     * @throws IllegalArgumentException se l'ID è null o negativo
     */
    public Optional<Vaccinazione> getVaccinazioneById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della vaccinazione non può essere null o negativo");
        }
        return vaccinazioneRepo.findByIdAndIsDeletedFalse(id);
    }

    /**
     * Salva una nuova vaccinazione
     * @param vaccinazione la vaccinazione da salvare
     * @return la vaccinazione salvata
     * @throws IllegalArgumentException se la vaccinazione è null, il tipo di vaccino è null o l'animale è null
     */
    public Vaccinazione salvaVaccinazione(Vaccinazione vaccinazione) {
        if (vaccinazione == null) {
            throw new IllegalArgumentException("La vaccinazione non può essere null");
        }
        if (vaccinazione.getIdTipoVaccino() == null) {
            throw new IllegalArgumentException("Il tipo di vaccino non può essere null");
        }
        if (vaccinazione.getIdAnimale() == null) {
            throw new IllegalArgumentException("L'animale non può essere null");
        }
        if (vaccinazione.getDataVaccinazione() == null) {
            throw new IllegalArgumentException("La data di vaccinazione non può essere null");
        }
        if (vaccinazione.getIsDeleted() == null) {
            vaccinazione.setIsDeleted(false);
        }
        return vaccinazioneRepo.save(vaccinazione);
    }

    /**
     * Aggiorna una vaccinazione esistente
     * @param vaccinazione la vaccinazione da aggiornare
     * @return la vaccinazione aggiornata
     * @throws IllegalArgumentException se la vaccinazione è null, l'ID non esiste o i dati obbligatori sono mancanti
     */
    public Vaccinazione aggiornaVaccinazione(Vaccinazione vaccinazione) {
        if (vaccinazione == null) {
            throw new IllegalArgumentException("La vaccinazione non può essere null");
        }
        if (vaccinazione.getId() == null || vaccinazione.getId() <= 0) {
            throw new IllegalArgumentException("L'ID della vaccinazione è obbligatorio per l'aggiornamento");
        }
        if (vaccinazioneRepo.findByIdAndIsDeletedFalse(vaccinazione.getId()).isEmpty()) {
            throw new IllegalArgumentException("Vaccinazione con ID " + vaccinazione.getId() + " non trovata");
        }
        if (vaccinazione.getIdTipoVaccino() == null) {
            throw new IllegalArgumentException("Il tipo di vaccino non può essere null");
        }
        if (vaccinazione.getIdAnimale() == null) {
            throw new IllegalArgumentException("L'animale non può essere null");
        }
        if (vaccinazione.getDataVaccinazione() == null) {
            throw new IllegalArgumentException("La data di vaccinazione non può essere null");
        }
        vaccinazione.setIsDeleted(false);
        return vaccinazioneRepo.save(vaccinazione);
    }

    /**
     * Elimina una vaccinazione
     * @param id l'ID della vaccinazione da eliminare
     * @throws IllegalArgumentException se l'ID è null, negativo o la vaccinazione non esiste
     */
    public void eliminaVaccinazione(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della vaccinazione non può essere null o negativo");
        }
        Optional<Vaccinazione> vaccinazione = vaccinazioneRepo.findByIdAndIsDeletedFalse(id);
        if (vaccinazione.isEmpty()) {
            throw new IllegalArgumentException("Vaccinazione con ID " + id + " non trovata");
        }
        Vaccinazione vaccinazioneToDelete = vaccinazione.get();
        vaccinazioneToDelete.setIsDeleted(true);
        vaccinazioneRepo.save(vaccinazioneToDelete);
    }
}
