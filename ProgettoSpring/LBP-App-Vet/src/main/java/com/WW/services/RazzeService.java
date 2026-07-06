package com.WW.services;

import com.WW.entities.Razze;
import com.WW.entities.Specie;
import com.WW.repositories.RazzeRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RazzeService {

    private final RazzeRepo razzeRepo;

    public RazzeService(RazzeRepo razzeRepo) {
        this.razzeRepo = razzeRepo;
    }

    /**
     * Visualizza tutte le razze
     * @return lista di tutte le razze
     */
    public List<Razze> visualizzaTutteRazze() {
        return razzeRepo.findAll();
    }

    /**
     * Ricerca razze per ID
     * @param id l'ID della razza
     * @return lista di razze corrispondenti
     * @throws IllegalArgumentException se l'ID è null o negativo
     */
    public List<Razze> findRazzeById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della razza non può essere null o negativo");
        }
        return razzeRepo.findRazzeById(id);
    }

    /**
     * Ricerca razze per nome
     * @param nome il nome della razza
     * @return lista di razze corrispondenti
     * @throws IllegalArgumentException se il nome è null o vuoto
     */
    public List<Razze> findByNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della razza non può essere null o vuoto");
        }
        return razzeRepo.findByNome(nome);
    }

    /**
     * Recupera una razza per ID
     * @param id l'ID della razza
     * @return la razza se presente
     * @throws IllegalArgumentException se l'ID è null o negativo
     */
    public Optional<Razze> getRazzaById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della razza non può essere null o negativo");
        }
        return razzeRepo.findById(id);
    }

    /**
     * Salva una nuova razza
     * @param razza la razza da salvare
     * @return la razza salvata
     * @throws IllegalArgumentException se i dati obbligatori sono mancanti
     */
    public Razze salvaRazza(Razze razza) {
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
        return razzeRepo.save(razza);
    }

    /**
     * Aggiorna una razza esistente
     * @param razza la razza da aggiornare
     * @return la razza aggiornata
     * @throws IllegalArgumentException se i dati sono non validi o la razza non esiste
     */
    public Razze aggiornaRazza(Razze razza) {
        if (razza == null) {
            throw new IllegalArgumentException("La razza non può essere null");
        }
        if (razza.getId() == null || razza.getId() <= 0) {
            throw new IllegalArgumentException("L'ID della razza è obbligatorio per l'aggiornamento");
        }
        if (!razzeRepo.existsById(razza.getId())) {
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

        return razzeRepo.save(razza);
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
        Optional<Razze> razza = razzeRepo.findById(id);
        if (razza.isEmpty()) {
            throw new IllegalArgumentException("Razza con ID " + id + " non trovata");
        }

        Razze razzeToDelete = razza.get();
        razzeToDelete.setDeleted(true);
        razzeRepo.save(razzeToDelete);
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
        if (!razzeRepo.existsById(id)) {
            throw new IllegalArgumentException("Razza con ID " + id + " non trovata");
        }
        razzeRepo.deleteById(id);
    }
}
