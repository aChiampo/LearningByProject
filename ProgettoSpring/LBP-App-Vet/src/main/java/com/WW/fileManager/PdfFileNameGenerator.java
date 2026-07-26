package com.WW.fileManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class PdfFileNameGenerator {

    private static final DateTimeFormatter FILE_NAME_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy_MM_dd");

    private PdfFileNameGenerator() {
    }

    public static String invoiceFileName(Integer invoiceId, Integer customerId) {
        validateId(invoiceId, "Invoice ID");
        validateId(customerId, "Customer ID");

        return "invoice_"
                + invoiceId
                + "_C"
                + customerId
                + "_"
                + LocalDateTime.now().format(FILE_NAME_DATE_FORMATTER)
                + ".pdf";
    }

    public static String receiptFileName(Integer paymentId, Integer customerId) {
        validateId(paymentId, "Payment ID");
        validateId(customerId, "Customer ID");

        return "receipt_"
                + paymentId
                + "_C"
                + customerId
                + "_"
                + LocalDateTime.now().format(FILE_NAME_DATE_FORMATTER)
                + ".pdf";
    }

    public static String recipeFileName(Integer recipeId, Integer animalId) {
        validateId(recipeId, "Recipe ID");
        validateId(animalId, "Animal ID");

        return "recipe_"
                + recipeId
                + "_A"
                + animalId
                + "_"
                + LocalDateTime.now().format(FILE_NAME_DATE_FORMATTER)
                + ".pdf";
    }

    private static void validateId(Integer id, String fieldName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than zero");
        }
    }
}
