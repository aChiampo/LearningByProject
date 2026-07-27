package com.WW.mailManager;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.WW.fileManager.TemplateService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

/**
 * Servizio per inviare email HTML basate sui template applicativi.
 */
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private static final String TEMPLATE_CONFERMA_REGISTRAZIONE = "mail/conferma-registrazione";
    private static final String TEMPLATE_CONFERMA_APPUNTAMENTO = "mail/conferma-appuntamento";
    private static final String TEMPLATE_NUOVA_FATTURA = "mail/nuova-fattura-disponibile";
    private static final String TEMPLATE_CANCELLAZIONE_APPUNTAMENTO = "mail/cancellazione-appuntamento";
    private static final String TEMPLATE_PROMEMORIA_VACCINAZIONE = "mail/promemoria-scadenza-vaccinazione";
    private static final String TEMPLATE_AVVISO_RITARDO = "mail/avviso-ritardo-appuntamento";

    private final JavaMailSender mailSender;
    private final TemplateService templateService;

    @Value("${app.mail.from}")
    private String mittente;
    /**
     * Invia l'email di conferma registrazione.
     *
     * @param destinatario indirizzo email del cliente
     * @param variabili dati usati dal template
     */
    public void inviaConfermaRegistrazione(String destinatario, Map<String, Object> variabili) {
        inviaEmailTemplate(
                destinatario,
                "Registrazione completata",
                TEMPLATE_CONFERMA_REGISTRAZIONE,
                variabili);
    }
    /**
     * Invia l'email di conferma appuntamento.
     *
     * @param destinatario indirizzo email del cliente
     * @param variabili dati usati dal template
     */
    public void inviaConfermaAppuntamento(String destinatario, Map<String, Object> variabili) {
        inviaEmailTemplate(
                destinatario,
                "Appuntamento confermato",
                TEMPLATE_CONFERMA_APPUNTAMENTO,
                variabili);
    }
    /**
     * Invia l'avviso di nuova fattura disponibile.
     *
     * @param destinatario indirizzo email del cliente
     * @param variabili dati usati dal template
     */
    public void inviaNuovaFatturaDisponibile(String destinatario, Map<String, Object> variabili) {
        inviaEmailTemplate(
                destinatario,
                "Nuova fattura disponibile",
                TEMPLATE_NUOVA_FATTURA,
                variabili);
    }
    /**
     * Invia l'email di cancellazione appuntamento.
     *
     * @param destinatario indirizzo email del cliente
     * @param variabili dati usati dal template
     */
    public void inviaCancellazioneAppuntamento(String destinatario, Map<String, Object> variabili) {
        inviaEmailTemplate(
                destinatario,
                "Appuntamento cancellato",
                TEMPLATE_CANCELLAZIONE_APPUNTAMENTO,
                variabili);
    }
    /**
     * Invia il promemoria di scadenza vaccinazione.
     *
     * @param destinatario indirizzo email del cliente
     * @param variabili dati usati dal template
     */
    public void inviaPromemoriaScadenzaVaccinazione(String destinatario, Map<String, Object> variabili) {
        inviaEmailTemplate(
                destinatario,
                "Promemoria scadenza vaccinazione",
                TEMPLATE_PROMEMORIA_VACCINAZIONE,
                variabili);
    }
    /**
     * Invia l'avviso di ritardo appuntamento.
     *
     * @param destinatario indirizzo email del cliente
     * @param variabili dati usati dal template
     */
    public void inviaAvvisoRitardoAppuntamento(String destinatario, Map<String, Object> variabili) {
        inviaEmailTemplate(
                destinatario,
                "Avviso ritardo appuntamento",
                TEMPLATE_AVVISO_RITARDO,
                variabili);
    }
    /**
     * Renderizza un template HTML e lo invia via email.
     *
     * @param destinatario indirizzo email del destinatario
     * @param oggetto oggetto dell'email
     * @param template nome del template Thymeleaf
     * @param variabili dati usati dal template
     */
    public void inviaEmailTemplate(
            String destinatario,
            String oggetto,
            String template,
            Map<String, Object> variabili) {

        validaInput(destinatario, oggetto, template);

        // Il template viene renderizzato prima della creazione del MimeMessage per isolare errori di contenuto.
        String html = templateService.templateRender(template, variabili);
        inviaEmailHtml(destinatario, oggetto, html);
    }

    private void inviaEmailHtml(String destinatario, String oggetto, String html) {
        try {
            MimeMessage messaggio = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(messaggio, "UTF-8");

            helper.setFrom(mittente);
            helper.setTo(destinatario);
            helper.setSubject(oggetto);
            helper.setText(html, true);

            mailSender.send(messaggio);
        } catch (MessagingException | MailException ex) {
            throw new IllegalStateException("Errore durante l'invio dell'email.", ex);
        }
    }

    private void validaInput(String destinatario, String oggetto, String template) {
        if (!StringUtils.hasText(destinatario)) {
            throw new IllegalArgumentException("Il destinatario email e' obbligatorio.");
        }
        if (!StringUtils.hasText(oggetto)) {
            throw new IllegalArgumentException("L'oggetto email e' obbligatorio.");
        }
        if (!StringUtils.hasText(template)) {
            throw new IllegalArgumentException("Il template email e' obbligatorio.");
        }
    }
}
