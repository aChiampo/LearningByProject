package com.WW.fileManager;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import com.WW.entities.Azienda;
import com.WW.entities.Animale;
import com.WW.entities.Pagamento;
import com.WW.entities.TipoVisita;
import com.WW.entities.Utente;
import com.WW.entities.Visita;

class PdfFileServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void creaFatturaPrivatoGeneraPdfDalTemplate() throws Exception {
        PdfFileService pdfFileService = pdfFileService();

        Path pdf = pdfFileService.creaFatturaPrivato(
                "receipt-test.pdf",
                Map.of(
                        "pagamento", pagamento(),
                        "visita", visita()));

        assertThat(pdf).exists();
        assertThat(Files.size(pdf)).isGreaterThan(0);
    }

    @Test
    void creaFatturaAziendaGeneraPdfDalTemplate() throws Exception {
        PdfFileService pdfFileService = pdfFileService();
        Utente clienteAzienda = clienteAzienda();

        Path pdf = pdfFileService.creaFatturaAzienda(
                "receipt-company-test.pdf",
                Map.of(
                        "pagamento", pagamento(clienteAzienda),
                        "visita", visita(clienteAzienda)));

        assertThat(pdf).exists();
        assertThat(Files.size(pdf)).isGreaterThan(0);
    }

    @Test
    void creaRicettaMedicaGeneraPdfDalTemplate() throws Exception {
        PdfFileService pdfFileService = pdfFileService();

        Path pdf = pdfFileService.creaRicettaMedica(
                "recipe-test.pdf",
                Map.of("visita", visita(cliente())));

        assertThat(pdf).exists();
        assertThat(Files.size(pdf)).isGreaterThan(0);
    }

    private PdfFileService pdfFileService() {
        PdfFileService pdfFileService = new PdfFileService(templateService());
        ReflectionTestUtils.setField(pdfFileService, "fattureFolder", tempDir.toString());
        ReflectionTestUtils.setField(pdfFileService, "ricetteFolder", tempDir.toString());
        return pdfFileService;
    }

    private TemplateService templateService() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML");
        resolver.setCharacterEncoding("UTF-8");

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        return new TemplateService(templateEngine);
    }

    private Pagamento pagamento() {
        return pagamento(cliente());
    }

    private Pagamento pagamento(Utente cliente) {
        return Pagamento.builder()
                .id(17)
                .data(LocalDate.of(2026, 7, 28))
                .tipoPagamento("Carta")
                .importoTotale(new BigDecimal("45.00"))
                .utente(cliente)
                .build();
    }

    private Visita visita() {
        return visita(cliente());
    }

    private Visita visita(Utente cliente) {
        return Visita.builder()
                .id(3)
                .tipoVisita(TipoVisita.builder()
                        .nome("Visita generale")
                        .prezzo(new BigDecimal("45.00"))
                        .build())
                .animale(Animale.builder()
                        .nome("Milo")
                        .utente(cliente)
                        .build())
                .veterinario(Utente.builder()
                        .nome("Giulia")
                        .cognome("Verdi")
                        .build())
                .build();
    }

    private Utente cliente() {
        return Utente.builder()
                .id(27)
                .nome("Mario")
                .cognome("Rossi")
                .email("mario.rossi@example.com")
                .telefono("3331234567")
                .indirizzo("Via Roma 1")
                .citta("Torino")
                .codiceFiscale("RSSMRA80A01L219U")
                .build();
    }

    private Utente clienteAzienda() {
        return Utente.builder()
                .id(28)
                .nome("Laura")
                .cognome("Bianchi")
                .email("laura.bianchi@example.com")
                .telefono("3339876543")
                .indirizzo("Corso Francia 10")
                .citta("Torino")
                .codiceFiscale("BNCLRA80A41L219V")
                .azienda(Azienda.builder()
                        .ragioneSociale("Vet Manager SRL")
                        .partitaIva("12345678901")
                        .formaGiuridica("SRL")
                        .build())
                .build();
    }
}
