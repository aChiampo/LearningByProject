package com.WW.controllers;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.WW.dto.DelayNotificationRequest;
import com.WW.dto.FirstAppointmentRequestDTO;
import com.WW.dto.PrenotazioneVisitaRequest;
import com.WW.dto.RiprogrammazioneVisitaRequest;
import com.WW.dto.RichiestaSlotDisponibiliDto;
import com.WW.dto.SlotDisponibileDto;
import com.WW.dto.VisitParamDTO;
import com.WW.dto.VisitaDto;
import com.WW.entities.Visita;
import com.WW.mailManager.EmailSenderService;
import com.WW.sicurezza.UtenteAutenticato;
import com.WW.services.VisitaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST per visite, prenotazioni, riprogrammazioni e pagamenti
 * collegati.
 */

@RestController
@RequestMapping("/api/visite")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VisitaController {

    private final VisitaService visitaService;
    private final EmailSenderService emailSenderService;

    @Value("${app.mail.first-appointment-recipients}")
    private List<String> destinatariPrimaVisita;

    /**
     * Restituisce tutte le visite.
     *
     * @return elenco delle visite
     */
    @GetMapping("/ottieniTutte")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<Visita>> ottieniTutte(Authentication authentication) {
        return ResponseEntity.ok(visitaService.getAllVisita(getClienteIdIfCliente(authentication)));
    }

    /**
     * Restituisce una visita a partire dall'id.
     *
     * @param id identificativo della visita
     * @return visita trovata
     */
    @GetMapping("/ottieni/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<Visita> ottieni(@PathVariable Integer id, Authentication authentication) {
        Visita visita = visitaService.getVisitaById(id);
        if (!canAccessVisit(visita, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(visita);
    }

    /**
     * Restituisce le visite associate a un tipo visita.
     *
     * @param idTipoVisita identificativo del tipo visita
     * @return visite filtrate per tipo
     */
    @GetMapping("/ottieniPerTipo/{idTipoVisita}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST')")
    public ResponseEntity<List<Visita>> ottieniPerTipo(@PathVariable Integer idTipoVisita) {
        return ResponseEntity.ok(visitaService.getVisiteByTipoVisita(idTipoVisita));
    }

    /**
     * Restituisce le visite associate a un veterinario.
     *
     * @param idVeterinario identificativo del veterinario
     * @return visite filtrate per veterinario
     */
    @GetMapping("/ottieniPerVeterinario/{idVeterinario}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST')")
    public ResponseEntity<List<Visita>> ottieniPerVeterinario(@PathVariable Integer idVeterinario) {
        return ResponseEntity.ok(visitaService.getVisiteByVeterinario(idVeterinario));
    }

    /**
     * Restituisce le visite associate a un animale.
     *
     * @param idAnimale identificativo dell'animale
     * @return visite filtrate per animale
     */
    @GetMapping("/ottieniPerAnimale/{idAnimale}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<Visita>> ottieniPerAnimale(@PathVariable Integer idAnimale,
            Authentication authentication) {
        return ResponseEntity.ok(filterAccessibleVisits(visitaService.getVisiteByAnimale(idAnimale), authentication));
    }

    /**
     * Restituisce la visita collegata a un pagamento.
     *
     * @param idPagamento identificativo del pagamento
     * @return visita trovata
     */
    @GetMapping("/ottieniPerPagamento/{idPagamento}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Visita> ottieniPerPagamento(@PathVariable Integer idPagamento) {
        return ResponseEntity.ok(visitaService.getVisitaByPagamento(idPagamento));
    }

    /**
     * Restituisce tutte le visite pagate.
     *
     * @return visite pagate
     */
    @GetMapping("/ottieniPagate")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<Visita>> ottieniPagate(Authentication authentication) {
        return ResponseEntity.ok(visitaService.OttieniVisitePagate(getClienteIdIfCliente(authentication)));
    }

    /**
     * Restituisce tutte le visite non pagate.
     *
     * @return visite non pagate
     */
    @GetMapping("/ottieniNonPagate")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<Visita>> ottieniNonPagate(Authentication authentication) {
        return ResponseEntity.ok(visitaService.OttieniVisiteNonPagate(getClienteIdIfCliente(authentication)));
    }

    /**
     * Restituisce tutte le visite pagate di un animale.
     *
     * @param idAnimale identificativo dell'animale
     * @return visite pagate dell'animale
     */
    @GetMapping("/ottieniPagatePerAnimale/{idAnimale}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<Visita>> ottieniPagatePerAnimale(@PathVariable int idAnimale,
            Authentication authentication) {
        return ResponseEntity
                .ok(filterAccessibleVisits(visitaService.OttieniVisitePagatebyAnimale(idAnimale), authentication));
    }

    /**
     * Restituisce tutte le visite non pagate di un animale.
     *
     * @param idAnimale identificativo dell'animale
     * @return visite non pagate dell'animale
     */
    @GetMapping("/ottieniNonPagatePerAnimale/{idAnimale}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<Visita>> ottieniNonPagatePerAnimale(@PathVariable int idAnimale,
            Authentication authentication) {
        return ResponseEntity
                .ok(filterAccessibleVisits(visitaService.OttieniVisiteNonPagateByAnimale(idAnimale), authentication));
    }

    /**
     * Restituisce le visite filtrate dai parametri specificati.
     *
     * @param params parametri opzionali di filtro
     * @return visite filtrate
     */
    @PostMapping("/params")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<VisitaDto>> ottieniPerParametri(
            @RequestBody(required = false) VisitParamDTO params,
            Authentication authentication) {
        return ResponseEntity.ok(visitaService.getVisiteByParams(applyRoleFilters(params, authentication)));
    }

    /**
     * Gestisce la richiesta HTTP per ottieniSlotDisponibili.
     *
     * @param richiesta      parametro richiesto dall'operazione
     * @param authentication parametro richiesto dall'operazione
     * @return risultato dell'operazione
     */

    @PostMapping("/slot-disponibili")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<List<SlotDisponibileDto>> ottieniSlotDisponibili(
            @Valid @RequestBody RichiestaSlotDisponibiliDto richiesta,
            Authentication authentication) {
        return ResponseEntity.ok(visitaService.ottieniSlotDisponibili(richiesta, authentication));
    }

    /**
     * Crea una nuova visita.
     *
     * @param visita dati della visita da salvare
     * @return visita creata
     */
    @PostMapping("/prenotazione")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Visita> prenota(@Valid @RequestBody VisitaDto visita) {
        Visita nuovaVisita = visitaService.createVisita(visita);
        return new ResponseEntity<Visita>(nuovaVisita, HttpStatus.CREATED);
    }

    /**
     * Gestisce la richiesta HTTP per prenotaSlot.
     *
     * @param richiesta      parametro richiesto dall'operazione
     * @param authentication parametro richiesto dall'operazione
     * @return risultato dell'operazione
     */

    @PostMapping("/prenota")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<Visita> prenotaSlot(
            @Valid @RequestBody PrenotazioneVisitaRequest richiesta,
            Authentication authentication) {
        Visita nuovaVisita = visitaService.prenotaVisita(richiesta, authentication);
        return new ResponseEntity<>(nuovaVisita, HttpStatus.CREATED);
    }

    /**
     * Completa una visita prenotata aggiornando tutti i dati ricevuti nel DTO.
     *
     * @param visita dati completi della visita da completare
     * @return visita aggiornata
     */
    @PostMapping("/chiudivisita")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<Visita> chiudiVisita(@RequestBody VisitaDto visita) {
        return ResponseEntity.ok(visitaService.chiudiVisita(visita));
    }

    /**
     * Riporta una visita completata allo stato prenotata.
     *
     * @param id identificativo della visita
     * @return visita aggiornata
     */
    @PatchMapping("/riaprivisita/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<Visita> riapriVisita(@PathVariable Integer id) {
        return ResponseEntity.ok(visitaService.riapriVisita(id));
    }

    /**
     * Aggiorna le note di una visita.
     *
     * @param id   identificativo della visita
     * @param note nuove note da salvare
     * @return visita aggiornata
     */
    @PatchMapping("/aggiornaNote/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VETERINARIO')")
    public ResponseEntity<Visita> aggiornaNote(
            @PathVariable Integer id,
            @RequestParam String note) {
        return ResponseEntity.ok(visitaService.updateVisitaNote(id, note));
    }

    /**
     * Aggiorna lo stato di pagamento di una visita.
     *
     * @param id        identificativo della visita
     * @param pagamento nuovo stato di pagamento
     * @return visita aggiornata
     */
    @PatchMapping("/aggiornaPagato/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST','CLIENTE')")
    public ResponseEntity<Visita> aggiornaPagato(@PathVariable Integer id,
            @RequestParam("pagamento") Integer pagamentoId) {
        return ResponseEntity.ok(visitaService.updateVisitaPagato(id, pagamentoId));
    }

    /**
     * Gestisce la richiesta HTTP per riprogramma.
     *
     * @param id             parametro richiesto dall'operazione
     * @param request        parametro richiesto dall'operazione
     * @param authentication parametro richiesto dall'operazione
     * @return risultato dell'operazione
     */

    @PatchMapping("/riprogramma/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<VisitaDto> riprogramma(
            @PathVariable Integer id,
            @Valid @RequestBody RiprogrammazioneVisitaRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(VisitaDto.fromEntity(visitaService.riprogrammaVisita(id, request, authentication)));
    }

    /**
     * Invia una notifica email al cliente per avvisare di un ritardo stimato.
     *
     * @param request dati della visita e ritardo stimato in minuti
     * @return risposta senza contenuto
     */
    @PostMapping("/notifica-ritardo")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Void> notificaRitardo(@Valid @RequestBody DelayNotificationRequest request) {
        visitaService.inviaNotificaRitardo(request.visitaId(), request.delayMinutes());
        return ResponseEntity.noContent().build();
    }

    /**
     * Elimina una visita esistente.
     *
     * @param id identificativo della visita da eliminare
     * @return risposta senza contenuto
     */
    @DeleteMapping("/elimina/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST', 'CLIENTE')")
    public ResponseEntity<Void> elimina(@PathVariable Integer id, Authentication authentication) {
        visitaService.deleteVisita(id, authentication);
        return ResponseEntity.noContent().build();
    }

    private Integer getClienteIdIfCliente(Authentication authentication) {
        if (!isCliente(authentication)) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UtenteAutenticato utenteAutenticato) {
            return utenteAutenticato.id();
        }

        return Integer.valueOf(authentication.getName());
    }

    private boolean isCliente(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_CLIENTE"));
    }

    private boolean isVeterinario(Authentication authentication) {
        return hasRole(authentication, "ROLE_VETERINARIO");
    }

    private boolean isReceptionist(Authentication authentication) {
        return hasRole(authentication, "ROLE_RECEPTIONIST");
    }

    private boolean isAdmin(Authentication authentication) {
        return hasRole(authentication, "ROLE_ADMIN");
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    private boolean canAccessVisit(Visita visita, Authentication authentication) {
        if (isAdmin(authentication) || isReceptionist(authentication) || isVeterinario(authentication)) {
            return true;
        }

        return isCliente(authentication)
                && visita.getAnimale() != null
                && visita.getAnimale().getUtente() != null
                && Objects.equals(visita.getAnimale().getUtente().getId(), getAuthenticatedUserId(authentication));
    }

    private List<Visita> filterAccessibleVisits(List<Visita> visite, Authentication authentication) {
        if (!isCliente(authentication)) {
            return visite;
        }

        Integer userId = getAuthenticatedUserId(authentication);
        return visite.stream()
                .filter(visita -> visita.getAnimale() != null
                        && visita.getAnimale().getUtente() != null
                        && Objects.equals(visita.getAnimale().getUtente().getId(), userId))
                .toList();
    }

    private VisitParamDTO applyRoleFilters(VisitParamDTO params, Authentication authentication) {
        if (isCliente(authentication)) {
            return new VisitParamDTO(
                    params == null ? null : params.date(),
                    params == null ? null : params.doctorID(),
                    getAuthenticatedUserId(authentication),
                    params == null ? null : params.animalID(),
                    params == null ? null : params.tipoVisitaID(),
                    params == null ? null : params.pagamentoID(),
                    params == null ? null : params.stato(),
                    params == null ? null : params.pagata());
        }

        if (isVeterinario(authentication)) {
            return new VisitParamDTO(
                    params == null ? null : params.date(),
                    getAuthenticatedUserId(authentication),
                    params == null ? null : params.clientID(),
                    params == null ? null : params.animalID(),
                    params == null ? null : params.tipoVisitaID(),
                    params == null ? null : params.pagamentoID(),
                    params == null ? null : params.stato(),
                    params == null ? null : params.pagata());
        }

        return params;
    }

    private Integer getAuthenticatedUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UtenteAutenticato utenteAutenticato) {
            return utenteAutenticato.id();
        }

        return Integer.valueOf(authentication.getName());
    }

    /**
     * Gestisce la richiesta HTTP per invia email di prima visita.
     * 
     * @param destinatario
     * @param variabili
     * @return
     */
    @PostMapping("prima-visita")
    public ResponseEntity<String> primaVisita(@Valid @RequestBody FirstAppointmentRequestDTO request) {
        emailSenderService.inviaEmailPrimaVisita(request);
        destinatariPrimaVisita.stream()
                .map(String::trim)
                .filter(destinatario -> !destinatario.isEmpty())
                .distinct()
                .forEach(destinatario -> emailSenderService.inviaEmailPrimaVisitaDottore(destinatario, request));
        return ResponseEntity.ok("Richiesta inviata correttamente.");
    }

}
