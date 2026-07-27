package com.WW.services;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.WW.entities.OrarioSettimanale;
import com.WW.repositories.OrariosettimanaleRepository;

import lombok.RequiredArgsConstructor;

/**
 * @author: cristian.pappalardo
 * Servizio per la tabella OrarioSettimanale
 * Last update: 28/08/2026
 */
@Service
@RequiredArgsConstructor
public class OrarioSettimanaleService {

	private final OrariosettimanaleRepository orariosettimanaleRepository;

	/**
	 * Salva un nuovo orario settimanale.
	 *
	 * @param orarioSettimanale dati da salvare
	 * @return orario creato
	 */
	public OrarioSettimanale aggiungiOrarioSettimanale(OrarioSettimanale orarioSettimanale) {
		return orariosettimanaleRepository.save(orarioSettimanale);
	}

	/**
	 * Aggiorna un orario settimanale esistente.
	 *
	 * @param id identificativo dell'orario da aggiornare
	 * @param modificato nuovi dati da applicare
	 * @return orario aggiornato
	 */
	public OrarioSettimanale modificaOrarioSettimanale(Integer id, OrarioSettimanale modificato) {
		OrarioSettimanale originale = ottieniPerId(id);

		if (modificato.getGiornoSettimana() != 0) {
			originale.setGiornoSettimana(modificato.getGiornoSettimana());
		}
		if (modificato.getMattinaInizio() != 0) {
			originale.setMattinaInizio(modificato.getMattinaInizio());
		}
		if (modificato.getMattinaFine() != 0) {
			originale.setMattinaFine(modificato.getMattinaFine());
		}
		if (modificato.getPomeriggioInizio() != 0) {
			originale.setPomeriggioInizio(modificato.getPomeriggioInizio());
		}
		if (modificato.getPomeriggioFine() != 0) {
			originale.setPomeriggioFine(modificato.getPomeriggioFine());
		}
		if (modificato.getUtente() != null) {
			originale.setUtente(modificato.getUtente());
		}

		return orariosettimanaleRepository.save(originale);
	}

	/**
	 * Restituisce tutti gli orari settimanali.
	 *
	 * @return elenco degli orari
	 */
	public List<OrarioSettimanale> ottieniTutti() {
		return orariosettimanaleRepository.findAll();
	}

	public Optional<OrarioSettimanale> ottieniPerUtenteEGiorno(Integer utenteId, int giornoSettimana) {
		return orariosettimanaleRepository.findByUtenteIdAndGiornoSettimana(utenteId, giornoSettimana);
	}

	public List<OrarioSettimanale> ottieniPerUtente(Integer utenteId) {
		return orariosettimanaleRepository.findByUtenteId(utenteId);
	}

	public OrarioSettimanale salva(OrarioSettimanale orarioSettimanale) {
		return orariosettimanaleRepository.save(orarioSettimanale);
	}

	/**
	 * Restituisce un orario settimanale dato l'id.
	 *
	 * @param id identificativo dell'orario
	 * @return orario trovato
	 */
	public OrarioSettimanale ottieniPerId(Integer id) {
		return orariosettimanaleRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Orario settimanale non trovato."));
	}

	/**
	 * Elimina un orario settimanale esistente.
	 *
	 * @param id identificativo dell'orario da eliminare
	 */
	public void eliminaOrarioSettimanale(Integer id) {
		if (!orariosettimanaleRepository.existsById(id)) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"Impossibile eliminare: Orario settimanale non trovato.");
		}
		orariosettimanaleRepository.deleteById(id);
	}

}
