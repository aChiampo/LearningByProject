package com.WW.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.WW.dto.RichiestaValutazioneAnimaleDto;
import com.WW.entities.Utente;
import com.WW.mailManager.EmailSenderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RichiestaValutazioneAnimaleService {

    private final EmailSenderService emailSenderService;
    private final UtenteService utenteService;

    @Value("${app.mail.vet-recipient:}")
    private String destinatarioVeterinario;

    @Value("${app.mail.from}")
    private String mittenteDefault;

    public void inviaRichiesta(Integer utenteId, RichiestaValutazioneAnimaleDto richiesta) {
        Utente cliente = utenteService.ottieniPerId(utenteId);

        Map<String, Object> variabili = new HashMap<>();
        variabili.put("nomeCliente", nomeCompleto(cliente));
        variabili.put("emailCliente", cliente.getEmail());
        variabili.put("telefonoCliente", valoreOpzionale(cliente.getTelefono()));
        variabili.put("nomeAnimale", richiesta.nome());
        variabili.put("specie", richiesta.specie());
        variabili.put("razza", richiesta.razza());
        variabili.put("sesso", richiesta.sesso());
        variabili.put("dataNascita", richiesta.dataNascita());
        variabili.put("peso", richiesta.peso());
        variabili.put("microchip", valoreOpzionale(richiesta.microchip()));
        variabili.put("note", valoreOpzionale(richiesta.note()));
        variabili.put("descrizione", richiesta.descrizione());

        emailSenderService.inviaRichiestaValutazioneAnimale(
                destinatarioEffettivo(),
                variabili);
    }

    private String destinatarioEffettivo() {
        return StringUtils.hasText(destinatarioVeterinario) ? destinatarioVeterinario : mittenteDefault;
    }

    private String nomeCompleto(Utente cliente) {
        return "%s %s".formatted(cliente.getNome(), cliente.getCognome()).trim();
    }

    private String valoreOpzionale(String valore) {
        return StringUtils.hasText(valore) ? valore : "Non indicato";
    }
}
