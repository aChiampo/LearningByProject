package com.WW.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final PasswordEncoder passwordEncoder;

    /**
     * Salva un nuovo utente.
     *
     * @param utente dati dell'utente da salvare
     * @return utente creato
     */
    @Transactional
    public Utente aggiungiUtente(Utente utente) {
        if (utente.getPasswordHash() != null && !utente.getPasswordHash().isBlank()) {
            utente.setPasswordHash(passwordEncoder.encode(utente.getPasswordHash()));
        }

        return utenteRepo.save(utente);
    }

    @Transactional
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
            originale.setPasswordHash(passwordEncoder.encode(modificato.getPasswordHash()));
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
        if (modificato.getCodiceFiscale() != null && !modificato.getCodiceFiscale().isBlank()) {
            originale.setCodiceFiscale(modificato.getCodiceFiscale());
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

    @Transactional
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
