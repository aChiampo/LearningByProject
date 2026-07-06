package com.WW.fileManager;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfFileService {

    private static final String TEMPLATE_FATTURA_AZIENDA = "fatture/fattura-azienda";
    private static final String TEMPLATE_FATTURA_PRIVATO = "fatture/fattura-privato";
    private static final String TEMPLATE_RICETTA_MEDICA = "ricette/ricetta-medica";

    private final TemplateService templateService;

    @Value("${app.files.fatture-folder}")
    private String fattureFolder;

    @Value("${app.files.ricette-folder}")
    private String ricetteFolder;

    public void creaFatturaAzienda(String fileName, Map<String, Object> variabili) {
        creaPdfFromTemplate(
                TEMPLATE_FATTURA_AZIENDA,
                variabili,
                fileName,
                fattureFolder
        );
    }

    public void creaFatturaPrivato(String fileName, Map<String, Object> variabili) {
        creaPdfFromTemplate(
                TEMPLATE_FATTURA_PRIVATO,
                variabili,
                fileName,
                fattureFolder
        );
    }

    public void creaRicettaMedica(String fileName, Map<String, Object> variabili) {
        creaPdfFromTemplate(
                TEMPLATE_RICETTA_MEDICA,
                variabili,
                fileName,
                ricetteFolder
        );
    }

    public Path creaPdfFromTemplate(
            String templateName,
            Map<String, Object> variables,
            String fileName,
            String folderPath) {

        String html = templateService.templateRender(templateName, variables);
        return createPdfFromHtml(html, fileName, folderPath);
    }

    /**
     * Creates a PDF file from an HTML string and stores it inside the provided folder.
     *
     * @param html HTML content that will be converted to PDF
     * @param fileName desired file name; ".pdf" is added automatically when missing( use PdfFileNameGenerator to generate standard file names)
     * @param folderPath folder where the PDF will be stored
     * @return absolute path of the saved PDF file
     */
    public Path createPdfFromHtml(String html, String fileName, String folderPath) {
        validateHtml(html);
        validateFolderPath(folderPath);

        String sanitizedFileName = sanitizeFileName(fileName);
        Path storageDirectory = Paths.get(folderPath).toAbsolutePath().normalize();
        Path finalPath = storageDirectory.resolve(sanitizedFileName).normalize();

        // Prevents filenames like "../../application.yml" from escaping the storage folder.
        if (!finalPath.startsWith(storageDirectory)) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }

        try {
            // Creates the folder and any missing parent folders before writing the PDF.
            Files.createDirectories(storageDirectory);

            try (OutputStream outputStream = Files.newOutputStream(finalPath)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();

                // The base URI lets relative assets in the HTML, such as images or CSS files,
                // be resolved starting from the storage folder.
                builder.withHtmlContent(html, storageDirectory.toUri().toString());
                builder.toStream(outputStream);
                builder.run();
            }

            return finalPath;
        } catch (Exception e) {
            throw new PdfFileServiceException("Could not create PDF file: " + finalPath, e);
        }
    }

    private void validateHtml(String html) {
        if (html == null || html.isBlank()) {
            throw new IllegalArgumentException("HTML cannot be empty");
        }
    }

    private void validateFolderPath(String folderPath) {
        if (folderPath == null || folderPath.isBlank()) {
            throw new IllegalArgumentException("Folder path cannot be empty");
        }
    }

    /**
     * Keeps only the last path segment and guarantees the ".pdf" extension.
     * Example: "../invoices/invoice-1" becomes "invoice-1.pdf".
     */
    private String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        String sanitizedFileName = Paths.get(fileName).getFileName().toString().trim();

        if (sanitizedFileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        if (!sanitizedFileName.toLowerCase().endsWith(".pdf")) {
            sanitizedFileName = sanitizedFileName + ".pdf";
        }

        return sanitizedFileName;
    }
}
