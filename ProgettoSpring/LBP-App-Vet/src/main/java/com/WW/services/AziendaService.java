package com.WW.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.WW.entities.Azienda;
import com.WW.repositories.AziendaRepository;

import lombok.RequiredArgsConstructor;
/**
 * Servizio per gestire aziende clienti e controlli di unicita della Partita IVA.
 */

@Service
@RequiredArgsConstructor
public class AziendaService {

    private final AziendaRepository aziendaRepository;

    // 1. CREA UNA NUOVA AZIENDA
    /**
     * Esegue la logica applicativa per creaAzienda.
     *
     * @param azienda parametro richiesto dall'operazione
     * @return risultato dell'operazione
     */
    @Transactional
    public Azienda creaAzienda(Azienda azienda) {
        if (azienda.getPartitaIva() != null && !azienda.getPartitaIva().isBlank()) {
            aziendaRepository.findByPartitaIvaAndIsDeletedFalse(azienda.getPartitaIva())
                    .ifPresent(a -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "La Partita IVA " + a.getPartitaIva() + " è già associata a un'altra azienda.");
                    });
        }
        if (azienda.getIsDeleted() == null) {
            azienda.setIsDeleted(false);
        }
        return aziendaRepository.save(azienda);
    }

    // 2. AGGIORNA UN'AZIENDA ESISTENTE
    /**
     * Esegue la logica applicativa per aggiornaAzienda.
     *
     * @param id parametro richiesto dall'operazione
     * @param datiAggiornati parametro richiesto dall'operazione
     * @return risultato dell'operazione
     */
    @Transactional
    public Azienda aggiornaAzienda(Integer id, Azienda datiAggiornati) {
        Azienda esistente = ottieniPerId(id); 

        if (datiAggiornati.getPartitaIva() != null && !datiAggiornati.getPartitaIva().isBlank()) {
            aziendaRepository.findByPartitaIvaAndIsDeletedFalse(datiAggiornati.getPartitaIva())
                    .filter(a -> !a.getId().equals(id))
                    .ifPresent(a -> {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "La Partita IVA " + a.getPartitaIva() + " è già associata a un'altra azienda.");
                    });
            esistente.setPartitaIva(datiAggiornati.getPartitaIva());
        }
        
        //Se i campi sono null o vuoti, rimangono invariati, altrimenti vengono aggiornati
        if (datiAggiornati.getRagioneSociale() != null && !datiAggiornati.getRagioneSociale().isBlank()) {
            esistente.setRagioneSociale(datiAggiornati.getRagioneSociale());
        }
        if (datiAggiornati.getFormaGiuridica() != null && !datiAggiornati.getFormaGiuridica().isBlank()) {
            esistente.setFormaGiuridica(datiAggiornati.getFormaGiuridica());
        }

        return aziendaRepository.save(esistente);
    }

    // 3. RECUPERA TUTTE LE AZIENDE
    /**
     * Esegue la logica applicativa per ottieniTutte.
     *
     * @return risultato dell'operazione
     */
    @Transactional(readOnly = true)
    public List<Azienda> ottieniTutte() {
        return aziendaRepository.findByIsDeletedFalse();
    }

    // 4. RECUPERA UNA SINGOLA AZIENDA TRAMITE ID
    /**
     * Esegue la logica applicativa per ottieniPerId.
     *
     * @param id parametro richiesto dall'operazione
     * @return risultato dell'operazione
     */
    @Transactional(readOnly = true)
    public Azienda ottieniPerId(Integer id) {
        return aziendaRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Azienda con ID " + id + " non trovata."));
    }

    // 5. ELIMINA UN'AZIENDA
    /**
     * Esegue la logica applicativa per eliminaAzienda.
     *
     * @param id parametro richiesto dall'operazione
     */
    @Transactional
    public void eliminaAzienda(Integer id) {
        Azienda azienda = aziendaRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Impossibile eliminare: Azienda non trovata."));
        azienda.setIsDeleted(true);
        aziendaRepository.save(azienda);
    }
}
