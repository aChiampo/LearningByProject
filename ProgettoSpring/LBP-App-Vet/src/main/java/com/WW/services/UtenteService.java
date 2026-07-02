package com.WW.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.WW.entities.Utente;
import com.WW.repositories.UtenteRepo;

import lombok.RequiredArgsConstructor;

/**
 * Servizio per la gestione degli utenti.
 */
@Service
@RequiredArgsConstructor
public class UtenteService {

    private final UtenteRepo utenteRepo;

    /**
     * Salva un nuovo utente.
     *
     * @param utente dati dell'utente da salvare
     * @return utente creato
     */
    public Utente aggiungiUtente(Utente utente) {
        return utenteRepo.save(utente);
    }

    /**
     * Aggiorna un utente esistente.
     *
     * @param id identificativo dell'utente da aggiornare
     * @param modificato nuovi dati da applicare
     * @return utente aggiornato
     */
    public Utente modificaUtente(Integer id, Utente modificato) {
        Utente originale = ottieniPerId(id);

        if (modificato.getNome() != null && !modificato.getNome().isBlank()) {
            originale.setNome(modificato.getNome());
        }
        if (modificato.getCognome() != null && !modificato.getCognome().isBlank()) {
            originale.setCognome(modificato.getCognome());
        }
        if (modificato.getEmail() != null && !modificato.getEmail().isBlank()) {
            originale.setEmail(modificato.getEmail());
        }
        if (modificato.getPasswordHash() != null && !modificato.getPasswordHash().isBlank()) {
            originale.setPasswordHash(modificato.getPasswordHash());
        }
        if (modificato.getTelefono() != null && !modificato.getTelefono().isBlank()) {
            originale.setTelefono(modificato.getTelefono());
        }
        if (modificato.getIndirizzo() != null) {
            originale.setIndirizzo(modificato.getIndirizzo());
        }
        if (modificato.getCitta() != null) {
            originale.setCitta(modificato.getCitta());
        }
        if (modificato.getRiferimento() != null) {
            originale.setRiferimento(modificato.getRiferimento());
        }

        return utenteRepo.save(originale);
    }

    /**
     * Restituisce tutti gli utenti.
     *
     * @return elenco degli utenti
     */
    public List<Utente> ottieniTutti() {
        return utenteRepo.findAll();
    }

    /**
     * Restituisce un utente dato l'id.
     *
     * @param id identificativo dell'utente
     * @return utente trovato
     */
    public Utente ottieniPerId(Integer id) {
        return utenteRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Utente non trovato."));
    }

    /**
     * Elimina un utente esistente.
     *
     * @param id identificativo dell'utente da eliminare
     */
    public void eliminaUtente(Integer id) {
        if (!utenteRepo.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Impossibile eliminare: Utente non trovato.");
        }
        utenteRepo.deleteById(id);
    }
}
