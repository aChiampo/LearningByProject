package com.WW.services;

import com.WW.dto.VaccinazioneDto;
import com.WW.dto.VaccinazioneRequest;
import com.WW.entities.Animale;
import com.WW.entities.TipoVaccino;
import com.WW.entities.Vaccinazione;
import com.WW.repositories.AnimaleRepository;
import com.WW.repositories.TipoVaccinoRepo;
import com.WW.repositories.VaccinazioneRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
/**
 * Servizio per gestire vaccinazioni e relative risposte DTO.
 */

@Service
public class VaccinazioneService {

    private final VaccinazioneRepo vaccinazioneRepo;
    private final TipoVaccinoRepo tipoVaccinoRepo;
    private final AnimaleRepository animaleRepository;

    public VaccinazioneService(
            VaccinazioneRepo vaccinazioneRepo,
            TipoVaccinoRepo tipoVaccinoRepo,
            AnimaleRepository animaleRepository) {
        this.vaccinazioneRepo = vaccinazioneRepo;
        this.tipoVaccinoRepo = tipoVaccinoRepo;
        this.animaleRepository = animaleRepository;
    }

    /**
     * Visualizza tutte le vaccinazioni effettuate.
     * 
     * @return lista di tutte le vaccinazioni
     */
    public List<Vaccinazione> visualizzaTutteVaccinazioni() {
        return vaccinazioneRepo.findByIsDeletedFalse();
    }

    /**
     * Visualizza tutte le vaccinazioni effettuate con risposta DTO.
     * 
     * @return lista DTO di tutte le vaccinazioni
     */
    @Transactional(readOnly = true)
    public List<VaccinazioneDto> visualizzaTutteVaccinazioniDto() {
        return vaccinazioneRepo.findByIsDeletedFalse().stream()
                .map(VaccinazioneDto::fromEntity)
                .toList();
    }

    /**
     * Ricerca vaccinazioni per animale con risposta DTO.
     * 
     * @param idAnimale identificativo dell'animale
     * @return lista DTO delle vaccinazioni dell'animale
     */
    @Transactional(readOnly = true)
    public List<VaccinazioneDto> findByIdAnimale(Integer idAnimale) {
        if (idAnimale == null || idAnimale <= 0) {
            throw new IllegalArgumentException("L'ID dell'animale non puo essere null o negativo");
        }

        return vaccinazioneRepo.findByIdAnimaleIdAndIsDeletedFalse(idAnimale).stream()
                .map(VaccinazioneDto::fromEntity)
                .toList();
    }

    /**
     * Recupera lo storico delle vaccinazioni entità di un determinato animale
     * ordinate per data.
     * 
     * @param idAnimale l'ID dell'animale
     * @return lista delle vaccinazioni dell'animale
     */
    public List<Vaccinazione> findByIdAnimaleStorico(Integer idAnimale) {
        if (idAnimale == null || idAnimale <= 0) {
            throw new IllegalArgumentException("L'ID dell'animale non puo essere null o negativo");
        }
        return vaccinazioneRepo.findByIdAnimaleIdAndIsDeletedFalseOrderByDataVaccinazioneDesc(idAnimale);
    }

    /**
     * Ricerca vaccinazioni per tipo di vaccino.
     * 
     * @param idTipoVaccino il tipo di vaccino
     * @return lista di vaccinazioni corrispondenti
     */
    public List<Vaccinazione> findByIdTipoVaccino(TipoVaccino idTipoVaccino) {
        if (idTipoVaccino == null) {
            throw new IllegalArgumentException("Il tipo di vaccino non puo essere null");
        }
        return vaccinazioneRepo.findByIdTipoVaccinoAndIsDeletedFalse(idTipoVaccino);
    }

    /**
     * Ricerca vaccinazioni per data di vaccinazione.
     * 
     * @param dataVaccinazione la data di vaccinazione
     * @return lista di vaccinazioni corrispondenti
     */
    public List<Vaccinazione> findByDataVaccinazione(LocalDateTime dataVaccinazione) {
        if (dataVaccinazione == null) {
            throw new IllegalArgumentException("La data di vaccinazione non puo essere null");
        }
        return vaccinazioneRepo.findByDataVaccinazioneAndIsDeletedFalse(dataVaccinazione);
    }

    /**
     * Ricerca vaccinazioni per lotto.
     * 
     * @param lotto il numero di lotto
     * @return lista di vaccinazioni corrispondenti
     */
    public List<Vaccinazione> findByLotto(String lotto) {
        if (lotto == null || lotto.trim().isEmpty()) {
            throw new IllegalArgumentException("Il numero di lotto non puo essere null o vuoto");
        }
        return vaccinazioneRepo.findByLottoAndIsDeletedFalse(lotto);
    }

    /**
     * Recupera una vaccinazione per ID.
     * 
     * @param id l'ID della vaccinazione
     * @return la vaccinazione se presente
     */
    public Optional<Vaccinazione> getVaccinazioneById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della vaccinazione non puo essere null o negativo");
        }
        return vaccinazioneRepo.findByIdAndIsDeletedFalse(id);
    }

    /**
     * Recupera una vaccinazione per ID con risposta DTO.
     * 
     * @param id l'ID della vaccinazione
     * @return DTO della vaccinazione se presente
     */
    @Transactional(readOnly = true)
    public Optional<VaccinazioneDto> getVaccinazioneDtoById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della vaccinazione non puo essere null o negativo");
        }
        return vaccinazioneRepo.findByIdAndIsDeletedFalse(id)
                .map(VaccinazioneDto::fromEntity);
    }

    /**
     * Salva una nuova vaccinazione da entità.
     * 
     * @param vaccinazione la vaccinazione da salvare
     * @return la vaccinazione salvata
     */
    public Vaccinazione salvaVaccinazione(Vaccinazione vaccinazione) {
        if (vaccinazione == null) {
            throw new IllegalArgumentException("La vaccinazione non puo essere null");
        }
        if (vaccinazione.getIdTipoVaccino() == null) {
            throw new IllegalArgumentException("Il tipo di vaccino non puo essere null");
        }
        if (vaccinazione.getIdAnimale() == null) {
            throw new IllegalArgumentException("L'animale non puo essere null");
        }
        if (vaccinazione.getDataVaccinazione() == null) {
            throw new IllegalArgumentException("La data di vaccinazione non puo essere null");
        }
        if (vaccinazione.getIsDeleted() == null) {
            vaccinazione.setIsDeleted(false);
        }
        return vaccinazioneRepo.save(vaccinazione);
    }

    /**
     * Salva una nuova vaccinazione partendo dal DTO di richiesta.
     * 
     * @param request dati della vaccinazione da salvare
     * @return DTO della vaccinazione salvata
     */
    @Transactional
    public VaccinazioneDto salvaVaccinazione(VaccinazioneRequest request) {
        return VaccinazioneDto.fromEntity(vaccinazioneRepo.save(toEntity(request, null)));
    }

    /**
     * Aggiorna una vaccinazione esistente da entità.
     * 
     * @param vaccinazione la vaccinazione da aggiornare
     * @return la vaccinazione aggiornata
     */
    public Vaccinazione aggiornaVaccinazione(Vaccinazione vaccinazione) {
        if (vaccinazione == null) {
            throw new IllegalArgumentException("La vaccinazione non puo essere null");
        }
        if (vaccinazione.getId() == null || vaccinazione.getId() <= 0) {
            throw new IllegalArgumentException("L'ID della vaccinazione e obbligatorio per l'aggiornamento");
        }
        if (vaccinazioneRepo.findByIdAndIsDeletedFalse(vaccinazione.getId()).isEmpty()) {
            throw new IllegalArgumentException("Vaccinazione con ID " + vaccinazione.getId() + " non trovata");
        }
        if (vaccinazione.getIdTipoVaccino() == null) {
            throw new IllegalArgumentException("Il tipo di vaccino non puo essere null");
        }
        if (vaccinazione.getIdAnimale() == null) {
            throw new IllegalArgumentException("L'animale non puo essere null");
        }
        if (vaccinazione.getDataVaccinazione() == null) {
            throw new IllegalArgumentException("La data di vaccinazione non puo essere null");
        }
        vaccinazione.setIsDeleted(false);
        return vaccinazioneRepo.save(vaccinazione);
    }

    /**
     * Aggiorna una vaccinazione esistente partendo dal DTO di richiesta.
     * 
     * @param id      identificativo della vaccinazione da aggiornare
     * @param request nuovi dati della vaccinazione
     * @return DTO della vaccinazione aggiornata
     */
    @Transactional
    public VaccinazioneDto aggiornaVaccinazione(Integer id, VaccinazioneRequest request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della vaccinazione e obbligatorio per l'aggiornamento");
        }
        if (vaccinazioneRepo.findByIdAndIsDeletedFalse(id).isEmpty()) {
            throw new IllegalArgumentException("Vaccinazione con ID " + id + " non trovata");
        }

        return VaccinazioneDto.fromEntity(vaccinazioneRepo.save(toEntity(request, id)));
    }

    /**
     * Elimina una vaccinazione eseguendo una soft delete.
     * 
     * @param id l'ID della vaccinazione da eliminare
     */
    public void eliminaVaccinazione(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("L'ID della vaccinazione non puo essere null o negativo");
        }
        Optional<Vaccinazione> vaccinazione = vaccinazioneRepo.findByIdAndIsDeletedFalse(id);
        if (vaccinazione.isEmpty()) {
            throw new IllegalArgumentException("Vaccinazione con ID " + id + " non trovata");
        }
        Vaccinazione vaccinazioneToDelete = vaccinazione.get();
        vaccinazioneToDelete.setIsDeleted(true);
        vaccinazioneRepo.save(vaccinazioneToDelete);
    }

    /**
     * Trova le vaccinazioni in scadenza entro un certo numero di giorni.
     * 
     * @param giorniFinestra giorni entro cui verificare la scadenza del richiamo
     * @return lista delle vaccinazioni in scadenza
     */
    public List<Vaccinazione> findVaccinazioniInScadenza(int giorniFinestra) {
        if (giorniFinestra < 0) {
            throw new IllegalArgumentException("I giorni di finestra non possono essere negativi");
        }
        LocalDateTime ora = LocalDateTime.now();
        LocalDateTime limite = ora.plusDays(giorniFinestra);

        return vaccinazioneRepo.findInScadenzaTra(ora, limite);
    }

    /**
     * Predispone e simula l'invio del promemoria al proprietario dell'animale.
     * 
     * @param idVaccinazione l'ID della vaccinazione
     * @return messaggio di conferma dell'invio
     */
    public String inviaPromemoriaVaccino(Integer idVaccinazione) {
        Vaccinazione vaccinazione = getVaccinazioneById(idVaccinazione)
                .orElseThrow(() -> new IllegalArgumentException("Vaccinazione non trovata con ID: " + idVaccinazione));

        String emailCliente = (vaccinazione.getIdAnimale() != null && vaccinazione.getIdAnimale().getUtente() != null)
                ? vaccinazione.getIdAnimale().getUtente().getEmail()
                : "cliente@email.com";

        System.out.println("Invio promemoria richiamo vaccino a: " + emailCliente);

        return "Promemoria inviato con successo a " + emailCliente;
    }

    private Vaccinazione toEntity(VaccinazioneRequest request, Integer id) {
        if (request == null) {
            throw new IllegalArgumentException("La vaccinazione non puo essere null");
        }
        if (request.tipoVaccinoId() == null || request.tipoVaccinoId() <= 0) {
            throw new IllegalArgumentException("Il tipo di vaccino non puo essere null o negativo");
        }
        if (request.animaleId() == null || request.animaleId() <= 0) {
            throw new IllegalArgumentException("L'animale non puo essere null o negativo");
        }
        if (request.dataVaccinazione() == null) {
            throw new IllegalArgumentException("La data di vaccinazione non puo essere null");
        }

        TipoVaccino tipoVaccino = tipoVaccinoRepo.findByIdAndIsDeletedFalse(request.tipoVaccinoId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tipo di vaccino con ID " + request.tipoVaccinoId() + " non trovato"));
        Animale animale = animaleRepository.findByIdAndIsDeletedFalse(request.animaleId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Animale con ID " + request.animaleId() + " non trovato"));

        Vaccinazione vaccinazione = new Vaccinazione();
        vaccinazione.setId(id);
        vaccinazione.setIdTipoVaccino(tipoVaccino);
        vaccinazione.setIdAnimale(animale);
        vaccinazione.setDataVaccinazione(request.dataVaccinazione());
        vaccinazione.setLotto(request.lotto());
        vaccinazione.setIsDeleted(false);
        return vaccinazione;
    }
}