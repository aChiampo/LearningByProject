package com.WW.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.WW.dto.UtenteDto;
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
        if (!utenteRepo.findByEmailAndIsDeletedFalse(utente.getEmail()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email già in uso.");
        }
        if (utente.getPasswordHash() != null && !utente.getPasswordHash().isBlank()) {
            utente.setPasswordHash(passwordEncoder.encode(utente.getPasswordHash()));
        }

        if (utente.getIsDeleted() == null) {
            utente.setIsDeleted(false);
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

    @Transactional
    public Utente modificaUtente(Integer id, UtenteDto modificato) {
        Utente originale = ottieniPerId(id);

        if (modificato.nome() != null && !modificato.nome().isBlank()) {
            originale.setNome(modificato.nome());
        }
        if (modificato.cognome() != null && !modificato.cognome().isBlank()) {
            originale.setCognome(modificato.cognome());
        }
        if (modificato.email() != null && !modificato.email().isBlank()) {
            originale.setEmail(modificato.email());
        }
        if (modificato.telefono() != null && !modificato.telefono().isBlank()) {
            originale.setTelefono(modificato.telefono());
        }
        if (modificato.indirizzo() != null) {
            originale.setIndirizzo(modificato.indirizzo());
        }
        if (modificato.citta() != null) {
            originale.setCitta(modificato.citta());
        }
        return utenteRepo.save(originale);
    }

    /**
     * Restituisce tutti gli utenti.
     *
     * @return elenco degli utenti
     */
    public List<Utente> ottieniTutti() {
        return utenteRepo.findByIsDeletedFalse();
    }

    /**
     * Restituisce un utente dato l'id.
     *
     * @param id identificativo dell'utente
     * @return utente trovato
     */
    public Utente ottieniPerId(Integer id) {
        return utenteRepo.findByIdAndIsDeletedFalse(id)
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
        Utente utente = utenteRepo.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Impossibile eliminare: Utente non trovato."));
        utente.setIsDeleted(true);
        utenteRepo.save(utente);
    }

    public Optional<Utente> ottieniPerEmail(String email) {
        return utenteRepo.findByEmailAndIsDeletedFalse(email);
    }
}
