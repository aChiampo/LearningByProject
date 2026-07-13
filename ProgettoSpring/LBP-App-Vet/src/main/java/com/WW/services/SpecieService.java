package com.WW.services;

import com.WW.entities.Specie;
import com.WW.repositories.SpecieRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SpecieService {

    private final SpecieRepo specieRepo;

    public SpecieService(SpecieRepo specieRepo) {
        this.specieRepo = specieRepo;
    }

    /**
     * Visualizza tutte le specie
     * @return lista di tutte le specie
     */
    public List<Specie> visualizzaTutteSpecie() {
        return specieRepo.findByIsDeletedFalse();
    }

    /**
     * Ricerca specie per nome
     * @param nome il nome della specie
     * @return lista di specie corrispondenti
     * @throws IllegalArgumentException se il nome è null o vuoto
     */
    public List<Specie> findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della specie non può essere null o vuoto");
        }
        return specieRepo.findByNomeAndIsDeletedFalse(nome);
    }

    /**
     * Recupera una specie per ID
     * @param id l'ID della specie
     * @return la specie se presente
     * @throws IllegalArgumentException se l'ID è null o negativo
     */
    public Optional<Specie> getSpecieById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della specie non può essere null o negativo");
        }
        return specieRepo.findByIdAndIsDeletedFalse(id);
    }

    /**
     * Salva una nuova specie
     * @param specie la specie da salvare
     * @return la specie salvata
     * @throws IllegalArgumentException se i dati obbligatori sono mancanti
     */
    public Specie salvaSpecie(Specie specie) {
        if (specie == null) {
            throw new IllegalArgumentException("La specie non può essere null");
        }
        if (specie.getNome() == null || specie.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della specie è obbligatorio");
        }

        // Verifica unicità del nome
        List<Specie> specieEsistenti = specieRepo.findByNomeAndIsDeletedFalse(specie.getNome().trim());
        if (!specieEsistenti.isEmpty()) {
            throw new IllegalArgumentException("Una specie con nome '" + specie.getNome() + "' esiste già");
        }

        specie.setDeleted(false);
        return specieRepo.save(specie);
    }

    /**
     * Aggiorna una specie esistente
     * @param specie la specie da aggiornare
     * @return la specie aggiornata
     * @throws IllegalArgumentException se i dati sono non validi o la specie non esiste
     */
    public Specie aggiornaSpecie(Specie specie) {
        if (specie == null) {
            throw new IllegalArgumentException("La specie non può essere null");
        }
        if (specie.getId() == null || specie.getId() <= 0) {
            throw new IllegalArgumentException("L'ID della specie è obbligatorio per l'aggiornamento");
        }
        if (specieRepo.findByIdAndIsDeletedFalse(specie.getId()).isEmpty()) {
            throw new IllegalArgumentException("Specie con ID " + specie.getId() + " non trovata");
        }
        if (specie.getNome() == null || specie.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della specie è obbligatorio");
        }

        // Verifica unicità del nome (escludendo l'ID corrente)
        List<Specie> specieEsistenti = specieRepo.findByNomeAndIsDeletedFalse(specie.getNome().trim());
        if (!specieEsistenti.isEmpty() && !specieEsistenti.get(0).getId().equals(specie.getId())) {
            throw new IllegalArgumentException("Una specie con nome '" + specie.getNome() + "' esiste già");
        }

        specie.setDeleted(false);
        return specieRepo.save(specie);
    }

    /**
     * Elimina una specie (soft delete)
     * @param id l'ID della specie da eliminare
     * @throws IllegalArgumentException se l'ID è null, negativo o la specie non esiste
     */
    public void eliminaSpecie(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della specie non può essere null o negativo");
        }
        Optional<Specie> specie = specieRepo.findByIdAndIsDeletedFalse(id);
        if (specie.isEmpty()) {
            throw new IllegalArgumentException("Specie con ID " + id + " non trovata");
        }

        Specie specieToDelete = specie.get();
        specieToDelete.setDeleted(true);
        specieRepo.save(specieToDelete);
    }

    /**
     * Elimina fisicamente una specie dal database
     * @param id l'ID della specie da eliminare
     * @throws IllegalArgumentException se l'ID è null, negativo o la specie non esiste
     */
    public void eliminaSpecieFisica(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della specie non può essere null o negativo");
        }
        eliminaSpecie(id);
    }
}
