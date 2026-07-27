package com.WW.config;

import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.WW.entities.OrarioSettimanale;
import com.WW.entities.Utente;
import com.WW.services.OrarioSettimanaleService;
import com.WW.services.UtenteService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrarioSettimanaleInitializer implements ApplicationRunner {

    private static final List<Integer> GIORNI_FERIALI = List.of(1, 2, 3, 4, 5);
    private static final int SABATO = 6;

    private final UtenteService utenteService;
    private final OrarioSettimanaleService orarioSettimanaleService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (Utente veterinario : utenteService.ottieniVeterinariAttivi()) {
            GIORNI_FERIALI.forEach(giorno -> salvaOrAggiorna(veterinario, giorno, 830, 1930, 0, 0));
            salvaOrAggiorna(veterinario, SABATO, 900, 1300, 0, 0);
        }
    }

    private void salvaOrAggiorna(
            Utente veterinario,
            int giornoSettimana,
            int mattinaInizio,
            int mattinaFine,
            int pomeriggioInizio,
            int pomeriggioFine) {
        OrarioSettimanale orario = orarioSettimanaleService
                .ottieniPerUtenteEGiorno(veterinario.getId(), giornoSettimana)
                .orElseGet(OrarioSettimanale::new);

        orario.setUtente(veterinario);
        orario.setGiornoSettimana(giornoSettimana);
        orario.setMattinaInizio(mattinaInizio);
        orario.setMattinaFine(mattinaFine);
        orario.setPomeriggioInizio(pomeriggioInizio);
        orario.setPomeriggioFine(pomeriggioFine);

        orarioSettimanaleService.salva(orario);
    }
}
