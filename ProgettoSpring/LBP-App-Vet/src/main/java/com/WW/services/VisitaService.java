package com.WW.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.WW.dto.PrenotazioneVisitaRequest;
import com.WW.dto.RichiestaSlotDisponibiliDto;
import com.WW.dto.SlotDisponibileDto;
import com.WW.dto.VisitParamDTO;
import com.WW.dto.VisitaDto;
import com.WW.entities.Animale;
import com.WW.entities.FileReferences;
import com.WW.entities.OrarioSettimanale;
import com.WW.entities.Pagamento;
import com.WW.entities.TipoVisita;
import com.WW.entities.Utente;
import com.WW.entities.Visita;
import com.WW.enums.VisitaStato;
import com.WW.fileManager.PdfFileNameGenerator;
import com.WW.fileManager.PdfFileService;
import com.WW.mailManager.EmailSenderService;
import com.WW.repositories.VisitaRepository;

/**
 * Servizio per la tabella VISITA.
 */
@Service
public class VisitaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VisitaService.class);

    private static final DateTimeFormatter MAIL_DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final VisitaRepository visitaRepository;
    private final TipoVisitaService tipoVisitaService;
    private final AnimaleService animaleService;
    private final UtenteService utenteService;
    private final OrarioSettimanaleService orarioSettimanaleService;
    private final PagamentiService pagamentiService;
    private final FileReferencesService fileReferencesService;
    private final PdfFileService pdfFileService;
    private final EmailSenderService emailSenderService;

    @Value("${app.clinic.name:Clinica Veterinaria}")
    private String clinicName;

    public VisitaService(
            VisitaRepository visitaRepository,
            TipoVisitaService tipoVisitaService,
            AnimaleService animaleService,
            UtenteService utenteService,
            OrarioSettimanaleService orarioSettimanaleService,
            PagamentiService pagamentiService,
            FileReferencesService fileReferencesService,
            PdfFileService pdfFileService,
            EmailSenderService emailSenderService) {
        this.visitaRepository = visitaRepository;
        this.tipoVisitaService = tipoVisitaService;
        this.animaleService = animaleService;
        this.utenteService = utenteService;
        this.orarioSettimanaleService = orarioSettimanaleService;
        this.pagamentiService = pagamentiService;
        this.fileReferencesService = fileReferencesService;
        this.pdfFileService = pdfFileService;
        this.emailSenderService = emailSenderService;
    }

    /**
     * Crea una nuova visita.
     *
     * @param visita dati della visita da salvare
     * @return visita creata
     */
    @Transactional
    public Visita createVisita(VisitaDto visita) {
        validaVisitaPerCreazione(visita);
        return visitaRepository.save(toEntity(visita));
    }

    @Transactional(readOnly = true)
    public List<SlotDisponibileDto> ottieniSlotDisponibili(
            RichiestaSlotDisponibiliDto richiesta,
            Authentication authentication) {
        if (richiesta == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La richiesta degli slot non puo essere nulla.");
        }

        Animale animale = animaleService.ottieniPerId(richiesta.animaleId());
        validaAccessoPrenotazione(animale, authentication);

        TipoVisita tipoVisita = tipoVisitaService.ottieniPerId(richiesta.tipoVisitaId());
        validaTipoVisitaAttivo(tipoVisita);

        Utente veterinario = utenteService.ottieniPerId(richiesta.veterinarioId());
        validaVeterinario(veterinario);

        List<SlotDisponibileDto> slotDisponibili = calcolaSlotDisponibili(
                animale,
                veterinario,
                tipoVisita,
                richiesta.data());

        if (slotDisponibili.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Nessuno slot disponibile. Seleziona un'altra data.");
        }

        return slotDisponibili;
    }

    @Transactional
    public Visita prenotaVisita(PrenotazioneVisitaRequest richiesta, Authentication authentication) {
        if (richiesta == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La richiesta di prenotazione non puo essere nulla.");
        }

        Animale animale = animaleService.bloccaPerPrenotazione(richiesta.animaleId());
        validaAccessoPrenotazione(animale, authentication);

        TipoVisita tipoVisita = tipoVisitaService.ottieniPerId(richiesta.tipoVisitaId());
        validaTipoVisitaAttivo(tipoVisita);

        Utente veterinario = utenteService.bloccaPerPrenotazione(richiesta.veterinarioId());
        validaVeterinario(veterinario);

        LocalDateTime inizio = richiesta.dataVisita();
        if (inizio == null || !inizio.isAfter(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lo slot selezionato deve essere futuro.");
        }

        LocalDateTime fine = inizio.plusMinutes(tipoVisita.getDurata());
        validaDisponibilitaCompleta(animale, veterinario, tipoVisita, inizio, fine, null);

        Visita visita = new Visita();
        visita.setAnimale(animale);
        visita.setTipoVisita(tipoVisita);
        visita.setVeterinario(veterinario);
        visita.setDataVisita(inizio);
        visita.setPagamento(null);
        visita.setNote(richiesta.note());
        visita.setNotaPrivata(null);
        visita.setStato(VisitaStato.PRENOTATA);
        visita.setIsDeleted(false);

        Visita salvata = visitaRepository.save(visita);
        inviaEmailConfermaPrenotazioneDopoCommit(salvata);
        return salvata;
    }

    private void validaVisitaPerCreazione(VisitaDto visita) {
        if (visita == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il corpo della visita non puo essere nullo.");
        }

        validaRiferimentiVisita(visita);

        TipoVisita tipoVisita = tipoVisitaService.ottieniPerId(visita.tipoVisita().id());
        validaTipoVisitaAttivo(tipoVisita);
        Utente veterinario = utenteService.ottieniPerId(visita.veterinario().id());
        validaVeterinario(veterinario);
        LocalDateTime inizio = ottieniDataInizio(visita);
        LocalDateTime fine = inizio.plusMinutes(tipoVisita.getDurata());
        validaDisponibilitaCompleta(
                animaleService.ottieniPerId(visita.animale().id()),
                veterinario,
                tipoVisita,
                inizio,
                fine,
                null);
    }

    private void validaRiferimentiVisita(VisitaDto visita) {
        if (visita.tipoVisita() == null || visita.tipoVisita().id() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il tipo visita e obbligatorio e deve contenere un id valido.");
        }
        if (visita.animale() == null || visita.animale().id() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'animale e obbligatorio e deve contenere un id valido.");
        }
        if (visita.veterinario() == null || visita.veterinario().id() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Il veterinario e obbligatorio e deve contenere un id valido.");
        }
        if (visita.pagamento() != null && visita.pagamento().id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il pagamento deve contenere un id valido.");
        }
    }

    private LocalDateTime ottieniDataInizio(VisitaDto visita) {
        try {
            return visita.getStartDateTime();
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    private Visita toEntity(VisitaDto visita) {
        Visita entity = new Visita();
        entity.setAnimale(animaleService.ottieniPerId(visita.animale().id()));
        entity.setTipoVisita(tipoVisitaService.ottieniPerId(visita.tipoVisita().id()));
        entity.setVeterinario(utenteService.ottieniPerId(visita.veterinario().id()));
        entity.setDataVisita(ottieniDataInizio(visita));
        if (visita.pagamento() != null) {
            entity.setPagamento(pagamentiService.ottieniPerId(visita.pagamento().id()));
        }
        entity.setNote(visita.note());
        entity.setNotaPrivata(visita.notaPrivata());
        entity.setStato(VisitaStato.PRENOTATA);
        entity.setIsDeleted(false);
        return entity;
    }

    private List<SlotDisponibileDto> calcolaSlotDisponibili(
            Animale animale,
            Utente veterinario,
            TipoVisita tipoVisita,
            LocalDate data) {
        OrarioSettimanale orario = orarioSettimanaleService
                .ottieniPerUtenteEGiorno(veterinario.getId(), data.getDayOfWeek().getValue())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Il veterinario non ha disponibilita per la data selezionata."));

        List<SlotDisponibileDto> slots = new ArrayList<>();
        aggiungiSlotIntervallo(slots, animale, veterinario, tipoVisita, data, orario.getMattinaInizio(), orario.getMattinaFine());
        aggiungiSlotIntervallo(slots, animale, veterinario, tipoVisita, data, orario.getPomeriggioInizio(), orario.getPomeriggioFine());
        return slots;
    }

    private void aggiungiSlotIntervallo(
            List<SlotDisponibileDto> slots,
            Animale animale,
            Utente veterinario,
            TipoVisita tipoVisita,
            LocalDate data,
            int intervalloInizio,
            int intervalloFine) {
        if (intervalloInizio <= 0 || intervalloFine <= 0 || intervalloFine <= intervalloInizio) {
            return;
        }

        LocalDateTime slotInizio = LocalDateTime.of(data, toLocalTime(intervalloInizio));
        LocalDateTime limiteFine = LocalDateTime.of(data, toLocalTime(intervalloFine));

        while (!slotInizio.plusMinutes(tipoVisita.getDurata()).isAfter(limiteFine)) {
            LocalDateTime slotFine = slotInizio.plusMinutes(tipoVisita.getDurata());

            if (slotInizio.isAfter(LocalDateTime.now())
                    && isDisponibileCompleto(animale, veterinario, tipoVisita, slotInizio, slotFine, null)) {
                slots.add(new SlotDisponibileDto(
                        slotInizio,
                        slotFine,
                        formattaOra(slotInizio) + " - " + formattaOra(slotFine)));
            }

            slotInizio = slotInizio.plusMinutes(10);
        }
    }

    private boolean isDisponibileCompleto(
            Animale animale,
            Utente veterinario,
            TipoVisita tipoVisita,
            LocalDateTime inizio,
            LocalDateTime fine,
            Integer visitaDaEscludereId) {
        return isDentroOrarioVeterinario(veterinario, inizio, fine)
                && !haSovrapposizioneVeterinario(veterinario, inizio, fine, visitaDaEscludereId)
                && !haSovrapposizioneAnimale(animale, inizio, fine, visitaDaEscludereId);
    }

    private void validaDisponibilitaCompleta(
            Animale animale,
            Utente veterinario,
            TipoVisita tipoVisita,
            LocalDateTime inizio,
            LocalDateTime fine,
            Integer visitaDaEscludereId) {
        if (!isDentroOrarioVeterinario(veterinario, inizio, fine)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "L'appuntamento deve iniziare e terminare negli orari di disponibilita del veterinario.");
        }
        if (haSovrapposizioneVeterinario(veterinario, inizio, fine, visitaDaEscludereId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Il veterinario ha gia una visita prenotata in questo intervallo orario.");
        }
        if (haSovrapposizioneAnimale(animale, inizio, fine, visitaDaEscludereId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "L'animale ha gia una visita prenotata in questo intervallo orario.");
        }
    }

    private boolean isDentroOrarioVeterinario(Utente veterinario, LocalDateTime inizio, LocalDateTime fine) {
        return orarioSettimanaleService
                .ottieniPerUtenteEGiorno(veterinario.getId(), inizio.getDayOfWeek().getValue())
                .map(orario -> isDentroIntervallo(orario.getMattinaInizio(), orario.getMattinaFine(), inizio, fine)
                        || isDentroIntervallo(orario.getPomeriggioInizio(), orario.getPomeriggioFine(), inizio, fine))
                .orElse(false);
    }

    private boolean isDentroIntervallo(int intervalloInizio, int intervalloFine, LocalDateTime inizio, LocalDateTime fine) {
        if (intervalloInizio <= 0 || intervalloFine <= 0 || intervalloFine <= intervalloInizio) {
            return false;
        }

        LocalDateTime apertura = LocalDateTime.of(inizio.toLocalDate(), toLocalTime(intervalloInizio));
        LocalDateTime chiusura = LocalDateTime.of(inizio.toLocalDate(), toLocalTime(intervalloFine));
        return !inizio.isBefore(apertura) && !fine.isAfter(chiusura);
    }

    private boolean haSovrapposizioneVeterinario(
            Utente veterinario,
            LocalDateTime inizio,
            LocalDateTime fine,
            Integer visitaDaEscludereId) {
        return visitaRepository.findVisiteVeterinarioNelPeriodo(
                        veterinario.getId(), inizio.minusDays(1), fine.plusDays(1))
                .stream()
                .filter(visita -> !Objects.equals(visita.getId(), visitaDaEscludereId))
                .filter(this::visitaBloccaSlot)
                .anyMatch(visita -> appuntamentiSovrapposti(inizio, fine, visita));
    }

    private boolean haSovrapposizioneAnimale(
            Animale animale,
            LocalDateTime inizio,
            LocalDateTime fine,
            Integer visitaDaEscludereId) {
        return visitaRepository.findVisiteAnimaleNelPeriodo(
                        animale.getId(), inizio.minusDays(1), fine.plusDays(1))
                .stream()
                .filter(visita -> !Objects.equals(visita.getId(), visitaDaEscludereId))
                .filter(this::visitaBloccaSlot)
                .anyMatch(visita -> appuntamentiSovrapposti(inizio, fine, visita));
    }

    private boolean appuntamentiSovrapposti(LocalDateTime inizio, LocalDateTime fine, Visita visita) {
        LocalDateTime esistenteInizio = visita.getDataVisita();
        LocalDateTime esistenteFine = esistenteInizio.plusMinutes(visita.getTipoVisita().getDurata());
        return inizio.isBefore(esistenteFine) && esistenteInizio.isBefore(fine);
    }

    private boolean visitaBloccaSlot(Visita visita) {
        if (visita.getStato() == null) {
            return true;
        }

        String stato = visita.getStato().name();
        return !stato.equals("CANCELLATA") && !stato.equals("ANNULLATA");
    }

    private void validaAccessoPrenotazione(Animale animale, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Autenticazione richiesta.");
        }

        if (haRuolo(authentication, "ROLE_CLIENTE")) {
            Integer utenteId = getAuthenticatedUserId(authentication);
            if (animale.getUtente() == null || !Objects.equals(animale.getUtente().getId(), utenteId)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Non puoi prenotare una visita per un animale di un altro cliente.");
            }
            return;
        }

        if (haRuolo(authentication, "ROLE_RECEPTIONIST") || haRuolo(authentication, "ROLE_ADMIN")) {
            return;
        }

        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Non hai i permessi per prenotare una visita.");
    }

    private void validaTipoVisitaAttivo(TipoVisita tipoVisita) {
        if (!tipoVisita.isAttivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il tipo di visita selezionato non e attivo.");
        }
    }

    private void validaVeterinario(Utente veterinario) {
        if (veterinario.getRuolo() == null || !"VETERINARIO".equals(veterinario.getRuolo().getRuolo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il veterinario selezionato non e valido.");
        }
    }

    private boolean haRuolo(Authentication authentication, String ruolo) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(ruolo::equals);
    }

    private Integer getAuthenticatedUserId(Authentication authentication) {
        try {
            return Integer.valueOf(authentication.getName());
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utente autenticato non valido.");
        }
    }

    private LocalTime toLocalTime(int valoreOrario) {
        if (valoreOrario < 24) {
            return LocalTime.of(valoreOrario, 0);
        }

        int ore = valoreOrario / 100;
        int minuti = valoreOrario % 100;
        return LocalTime.of(ore, minuti);
    }

    private String formattaOra(LocalDateTime dataOra) {
        return dataOra.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    @Transactional(readOnly = true)
    public List<Visita> getAllVisita() {
        return visitaRepository.findByIsDeletedFalse();
    }

    @Transactional(readOnly = true)
    public List<Visita> getAllVisita(Integer clienteId) {
        if (clienteId == null) {
            return visitaRepository.findByIsDeletedFalse();
        }

        return visitaRepository.findByAnimaleUtenteIdAndIsDeletedFalse(clienteId);
    }

    @Transactional(readOnly = true)
    public List<VisitaDto> getVisiteByParams(VisitParamDTO params) {
        return getAllVisita().stream()
                .filter(visita -> params == null || params.date() == null
                        || !visita.getDataVisita().isBefore(params.date()))
                .filter(visita -> params == null || params.doctorID() == null
                        || Objects.equals(visita.getVeterinario().getId(), params.doctorID()))
                .filter(visita -> params == null || params.clientID() == null
                        || Objects.equals(visita.getAnimale().getUtente().getId(), params.clientID()))
                .filter(visita -> params == null || params.animalID() == null
                        || Objects.equals(visita.getAnimale().getId(), params.animalID()))
                .filter(visita -> params == null || params.tipoVisitaID() == null
                        || Objects.equals(visita.getTipoVisita().getId(), params.tipoVisitaID()))
                .filter(visita -> params == null || params.pagamentoID() == null
                        || visita.getPagamento() != null
                                && Objects.equals(visita.getPagamento().getId(), params.pagamentoID()))
                .filter(visita -> params == null || params.stato() == null
                        || visita.getStato() == params.stato())
                .filter(visita -> params == null || params.pagata() == null
                        || Objects.equals(visita.getPagamento() != null, params.pagata()))
                .map(this::toDto)
                .toList();
    }

    /**
     * Converte un'entita Visita in un DTO VisitaDto.
     *
     * @param visita entita Visita da convertire
     * @return DTO VisitaDto corrispondente
     */
    public VisitaDto toDto(Visita visita) {
        return VisitaDto.fromEntity(visita);
    }

    @Transactional(readOnly = true)
    public Visita getVisitaById(Integer id) {
        return visitaRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Visita non trovata."));
    }

    @Transactional(readOnly = true)
    public List<Visita> getVisiteByTipoVisita(Integer idTipoVisita) {
        return visitaRepository.findByTipoVisitaIdAndIsDeletedFalse(idTipoVisita);
    }

    @Transactional(readOnly = true)
    public List<Visita> getVisiteByVeterinario(Integer idVeterinario) {
        return visitaRepository.findByVeterinarioIdAndIsDeletedFalse(idVeterinario);
    }

    @Transactional(readOnly = true)
    public List<Visita> getVisiteByAnimale(Integer idAnimale) {
        return visitaRepository.findByAnimaleIdAndIsDeletedFalse(idAnimale);
    }

    @Transactional(readOnly = true)
    public Visita getVisitaByPagamento(Integer idPagamento) {
        return visitaRepository.findByPagamentoIdAndIsDeletedFalse(idPagamento)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Visita collegata al pagamento non trovata."));
    }

    @Transactional
    public Visita updateVisitaPagato(Integer id, Integer pagamentoId) {
        Visita visita = getVisitaById(id);
        Pagamento pagato = pagamentiService.ottieniPerId(pagamentoId);
        Pagamento pagamentoConRicevuta = generaRicevutaSeNecessaria(visita, pagato);

        visita.setPagamento(pagamentoConRicevuta);
        return visitaRepository.save(visita);
    }

    private Pagamento generaRicevutaSeNecessaria(Visita visita, Pagamento pagamento) {
        if (pagamento.getRiferimentoFile() != null) {
            return pagamento;
        }

        Utente proprietario = visita.getAnimale().getUtente();

        if (proprietario == null || proprietario.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Impossibile generare la ricevuta: proprietario non trovato.");
        }

        if (pagamento.getUtente() == null) {
            pagamento.setUtente(proprietario);
        }

        String fileName = PdfFileNameGenerator.receiptFileName(pagamento.getId(), proprietario.getId());
        Map<String, Object> variabili = Map.of(
                "pagamento", pagamento,
                "visita", visita);

        Path receiptPath = proprietario.getAzienda() != null
                ? pdfFileService.creaFatturaAzienda(fileName, variabili)
                : pdfFileService.creaFatturaPrivato(fileName, variabili);

        try {
            FileReferences riferimentoFile = FileReferences.builder()
                    .originalFileName(fileName)
                    .storedFileName(receiptPath.getFileName().toString())
                    .mimeType("application/pdf")
                    .ssize(Files.size(receiptPath))
                    .storagePath(receiptPath.getParent().toString())
                    .owner(proprietario)
                    .uploadDate(LocalDateTime.now())
                    .isDeleted(false)
                    .build();

            pagamento.setRiferimentoFile(fileReferencesService.salvaFileReference(riferimentoFile));
            return pagamentiService.salvaPagamento(pagamento);
        } catch (Exception ex) {
            throw new IllegalStateException("Impossibile salvare il riferimento alla ricevuta.", ex);
        }
    }

    @Transactional(readOnly = true)
    public void inviaNotificaRitardo(Integer visitaId, Integer delayMinutes) {
        if (delayMinutes == null || delayMinutes <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il ritardo deve essere maggiore di zero.");
        }

        Visita visita = getVisitaById(visitaId);
        Utente cliente = visita.getAnimale().getUtente();

        if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Impossibile inviare la notifica: email cliente non disponibile.");
        }

        LocalDateTime appointmentTime = visita.getDataVisita();
        Map<String, Object> variabili = Map.of(
                "clientName", getFullName(cliente),
                "animalName", valueOrFallback(visita.getAnimale().getNome(), "il tuo animale"),
                "appointmentTime", MAIL_DATE_TIME_FORMATTER.format(appointmentTime),
                "delayMinutes", delayMinutes + " minuti",
                "estimatedNewTime", MAIL_DATE_TIME_FORMATTER.format(appointmentTime.plusMinutes(delayMinutes)),
                "clinicName", valueOrFallback(clinicName, "la clinica"));

        emailSenderService.inviaAvvisoRitardoAppuntamento(cliente.getEmail(), variabili);
    }

    private String getFullName(Utente utente) {
        String fullName = (valueOrFallback(utente.getNome(), "") + " " + valueOrFallback(utente.getCognome(), ""))
                .trim();

        if (!fullName.isBlank()) {
            return fullName;
        }

        return utente.getEmail() != null ? utente.getEmail() : "Cliente";
    }

    private String valueOrFallback(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    @Transactional
    public Visita chiudiVisita(VisitaDto visitaDto) {
        if (visitaDto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il corpo della visita non puo essere nullo.");
        }
        if (visitaDto.id() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'id della visita e obbligatorio.");
        }

        Visita visita = getVisitaById(visitaDto.id());
        if (visita.getStato() != VisitaStato.PRENOTATA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo una visita in stato PRENOTATA puo essere completata.");
        }

        aggiornaDatiVisita(visita, visitaDto);
        visita.setStato(VisitaStato.COMPLETATA);
        return visitaRepository.save(visita);
    }

    @Transactional
    public Visita riapriVisita(Integer id) {
        Visita visita = getVisitaById(id);
        if (visita.getStato() != VisitaStato.COMPLETATA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo una visita in stato COMPLETATA puo tornare a PRENOTATA.");
        }

        visita.setStato(VisitaStato.PRENOTATA);
        return visitaRepository.save(visita);
    }

    private void aggiornaDatiVisita(Visita visita, VisitaDto visitaDto) {
        validaRiferimentiVisita(visitaDto);

        TipoVisita tipoVisita = tipoVisitaService.ottieniPerId(visitaDto.tipoVisita().id());
        validaTipoVisitaAttivo(tipoVisita);
        Utente veterinario = utenteService.ottieniPerId(visitaDto.veterinario().id());
        validaVeterinario(veterinario);
        LocalDateTime dataInizio = ottieniDataInizio(visitaDto);
        LocalDateTime dataFine = dataInizio.plusMinutes(tipoVisita.getDurata());

        validaDisponibilitaCompleta(
                animaleService.ottieniPerId(visitaDto.animale().id()),
                veterinario,
                tipoVisita,
                dataInizio,
                dataFine,
                visita.getId());

        visita.setAnimale(animaleService.ottieniPerId(visitaDto.animale().id()));
        visita.setTipoVisita(tipoVisita);
        visita.setVeterinario(veterinario);
        visita.setDataVisita(dataInizio);
        visita.setPagamento(visitaDto.pagamento() != null
                ? pagamentiService.ottieniPerId(visitaDto.pagamento().id())
                : null);
        visita.setNote(visitaDto.note());
        visita.setNotaPrivata(visitaDto.notaPrivata());
    }

    @Transactional
    public Visita updateVisitaNote(Integer id, String note) {
        Visita visita = getVisitaById(id);
        visita.setNote(note);
        return visitaRepository.save(visita);
    }

    @Transactional
    public void deleteVisita(Integer id) {
        Visita visita = visitaRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Impossibile eliminare: Visita non trovata."));
        visita.setIsDeleted(true);
        visitaRepository.save(visita);
    }

    @Transactional(readOnly = true)
    public List<Visita> OttieniVisitePagate() {
        return OttieniVisitePagate(null);
    }

    @Transactional(readOnly = true)
    public List<Visita> OttieniVisitePagate(Integer clienteId) {
        if (clienteId == null) {
            return visitaRepository.findByPagamentoIsNotNullAndIsDeletedFalse();
        }

        return visitaRepository.findByPagamentoIsNotNullAndAnimaleUtenteIdAndIsDeletedFalse(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Visita> OttieniVisiteNonPagate() {
        return OttieniVisiteNonPagate(null);
    }

    @Transactional(readOnly = true)
    public List<Visita> OttieniVisiteNonPagate(Integer clienteId) {
        if (clienteId == null) {
            return visitaRepository.findByPagamentoIsNullAndIsDeletedFalse();
        }

        return visitaRepository.findByPagamentoIsNullAndAnimaleUtenteIdAndIsDeletedFalse(clienteId);
    }

    public List<Visita> OttieniVisitePagatebyAnimale(int idAnimale) {
        return visitaRepository.findByIsDeletedFalse().stream()
                .filter(visita -> visita.getPagamento() != null)
                .filter(visita -> visita.getAnimale().getId() == idAnimale)
                .toList();
    }

    public List<Visita> OttieniVisiteNonPagateByAnimale(int idAnimale) {
        return visitaRepository.findByIsDeletedFalse().stream()
                .filter(visita -> visita.getPagamento() == null)
                .filter(visita -> visita.getAnimale().getId() == idAnimale)
                .toList();
    }

    public Visita ottieniVisitaByData(LocalDateTime dataVisita) {
        return visitaRepository.findByDataVisitaAndIsDeletedFalse(dataVisita)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Visita non trovata per la data specificata."));
    }

    private void inviaEmailConfermaPrenotazione(Visita visita) {
        Utente cliente = visita.getAnimale().getUtente();

        if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Impossibile inviare la conferma: email cliente non disponibile.");
        }

        Map<String, Object> variabili = Map.of(
                "clientName", getFullName(cliente),
                "animalName", valueOrFallback(visita.getAnimale().getNome(), "il tuo animale"),
                "appointmentDateTime", MAIL_DATE_TIME_FORMATTER.format(visita.getDataVisita()),
                "visitType", valueOrFallback(visita.getTipoVisita().getNome(), "Visita"),
                "doctorName", getFullName(visita.getVeterinario()),
                "clinicName", valueOrFallback(clinicName, "la clinica"));

        emailSenderService.inviaConfermaAppuntamento(cliente.getEmail(), variabili);
    }

    private void inviaEmailConfermaPrenotazioneDopoCommit(Visita visita) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            inviaEmailConfermaPrenotazioneSenzaBloccarePrenotazione(visita);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                inviaEmailConfermaPrenotazioneSenzaBloccarePrenotazione(visita);
            }
        });
    }

    private void inviaEmailConfermaPrenotazioneSenzaBloccarePrenotazione(Visita visita) {
        try {
            inviaEmailConfermaPrenotazione(visita);
        } catch (RuntimeException ex) {
            LOGGER.warn(
                    "Prenotazione {} salvata, ma invio email di conferma non riuscito.",
                    visita.getId(),
                    ex);
        }
    }
}
