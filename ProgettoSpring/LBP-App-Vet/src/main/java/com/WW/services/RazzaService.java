package com.WW.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.WW.entities.Razza;
import com.WW.repositories.RazzaRepo;
/**
 * Servizio per gestire le razze animali e le ricerche per specie.
 */

@Service
public class RazzaService {

    private final RazzaRepo razzaRepo;

    public RazzaService(RazzaRepo razzaRepo) {
        this.razzaRepo = razzaRepo;
    }

    /**
     * Visualizza tutte le razze
     * @return lista di tutte le razze
     */
    public List<Razza> visualizzaTutteRazze() {
        return razzaRepo.findByIsDeletedFalse();
    }

    /**
     * Ricerca razze per ID della specie
     * @param specieId l'ID della specie
     * @return lista di razze corrispondenti alla specie
     * @throws IllegalArgumentException se l'ID della specie è null o negativo
     */
    public List<Razza> findByIdSpecie_Id(Integer specieId) {
        if (specieId == null || specieId <= 0) {
            throw new IllegalArgumentException("L'ID della specie non può essere null o negativo");
        }
        return razzaRepo.findByIdSpecie_IdAndIsDeletedFalse(specieId);
    }

    /**
     * Ricerca razze per nome
     * @param nome il nome della razza
     * @return lista di razze corrispondenti
     * @throws IllegalArgumentException se il nome è null o vuoto
     */
    public List<Razza> findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della razza non può essere null o vuoto");
        }
        return razzaRepo.findByNomeAndIsDeletedFalse(nome);
    }

    /**
     * Recupera una razza per ID
     * @param id l'ID della razza
     * @return la razza se presente
     * @throws IllegalArgumentException se l'ID è null o negativo
     */
    public Optional<Razza> getRazzaById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della razza non può essere null o negativo");
        }
        return razzaRepo.findByIdAndIsDeletedFalse(id);
    }

    /**
     * Salva una nuova razza
     * @param razza la razza da salvare
     * @return la razza salvata
     * @throws IllegalArgumentException se i dati obbligatori sono mancanti
     */
    public Razza salvaRazza(Razza razza) {
        if (razza == null) {
            throw new IllegalArgumentException("La razza non può essere null");
        }
        if (razza.getNome() == null || razza.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della razza è obbligatorio");
        }
        if (razza.getIdSpecie() == null) {
            throw new IllegalArgumentException("La specie della razza è obbligatoria");
        }
        if (razza.getIdSpecie().getId() == null || razza.getIdSpecie().getId() <= 0) {
            throw new IllegalArgumentException("L'ID della specie è obbligatorio e deve essere valido");
        }

        razza.setDeleted(false);
        return razzaRepo.save(razza);
    }

    /**
     * Aggiorna una razza esistente
     * @param razza la razza da aggiornare
     * @return la razza aggiornata
     * @throws IllegalArgumentException se i dati sono non validi o la razza non esiste
     */
    public Razza aggiornaRazza(Razza razza) {
        if (razza == null) {
            throw new IllegalArgumentException("La razza non può essere null");
        }
        if (razza.getId() == null || razza.getId() <= 0) {
            throw new IllegalArgumentException("L'ID della razza è obbligatorio per l'aggiornamento");
        }
        if (razzaRepo.findByIdAndIsDeletedFalse(razza.getId()).isEmpty()) {
            throw new IllegalArgumentException("Razza con ID " + razza.getId() + " non trovata");
        }
        if (razza.getNome() == null || razza.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della razza è obbligatorio");
        }
        if (razza.getIdSpecie() == null) {
            throw new IllegalArgumentException("La specie della razza è obbligatoria");
        }
        if (razza.getIdSpecie().getId() == null || razza.getIdSpecie().getId() <= 0) {
            throw new IllegalArgumentException("L'ID della specie è obbligatorio e deve essere valido");
        }

        razza.setDeleted(false);
        return razzaRepo.save(razza);
    }

    /**
     * Elimina una razza (soft delete)
     * @param id l'ID della razza da eliminare
     * @throws IllegalArgumentException se l'ID è null, negativo o la razza non esiste
     */
    public void eliminaRazza(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della razza non può essere null o negativo");
        }
        Optional<Razza> razza = razzaRepo.findByIdAndIsDeletedFalse(id);
        if (razza.isEmpty()) {
            throw new IllegalArgumentException("Razza con ID " + id + " non trovata");
        }

        Razza razzeToDelete = razza.get();
        razzeToDelete.setDeleted(true);
        razzaRepo.save(razzeToDelete);
    }

    /**
     * Elimina fisicamente una razza dal database
     * @param id l'ID della razza da eliminare
     * @throws IllegalArgumentException se l'ID è null, negativo o la razza non esiste
     */
    public void eliminaRazzaFisica(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della razza non può essere null o negativo");
        }
        eliminaRazza(id);
    }
}
