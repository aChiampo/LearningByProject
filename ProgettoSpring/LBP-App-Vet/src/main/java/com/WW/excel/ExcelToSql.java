package com.WW.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Converte un file Excel in istruzioni SQL INSERT.
 * Per ogni foglio: legge l'intestazione, la confronta con TABLE_SCHEMAS
 * per capire a quale tabella corrisponde, rimappa le colonne sullo
 * schema reale del DB tramite TARGET_MAPPINGS, e genera gli INSERT.
 */
public class ExcelToSql {

    public static void main(String[] args) throws IOException {

        String inputFile = "VetPortal_DatiImportazione.xlsx";
        String outputFile = inputFile.replace(".xlsx", ".sql");

        String excelFilePath = new File("").getAbsolutePath() + "/DatiExcel/" + inputFile;
        String outputSqlPath = new File("").getAbsolutePath() + "/DatiExcel/" + outputFile;

        String sql = generateSql(excelFilePath);

        try (FileWriter writer = new FileWriter(outputSqlPath)) {
            writer.write(sql);
        }

        System.out.println("SQL script generated: " + outputSqlPath);
    }

    // ------------------------------------------------------------------
    // Schemi
    // ------------------------------------------------------------------

    /** Colonne attese per ogni foglio, usate solo per identificare la tabella. */
    private static final Map<String, List<String>> TABLE_SCHEMAS = Map.of(
            // Aggiungere qui la struttura delle tabelle excel.

            "PROPRIETARI", List.of(
                    "ID", "Nome", "Cognome", "Email",
                    "Telefono", "Indirizzo", "Città",
                    "Data_Registrazione", "Riferimento"),

            "ANIMALI", List.of(
                    "ID", "Nome", "Specie", "Razza",
                    "Data_Nascita", "Sesso", "Peso_(kg)",
                    "Microchip", "Proprietario", "Note_Generali"),

            "VACCINAZIONI", List.of(
                    "ID", "ID_Animale", "Tipo_Vaccino",
                    "Data_Somministrazione", "Data_Scadenza",
                    "Lotto", "Stato", "Note"),

            "VISITE", List.of(
                    "ID", "ID_Animale", "Data_Visita",
                    "Tipo_Visita", "Motivo_/_Diagnosi",
                    "Trattamento", "Note", "Importo_(€)"),

            "TIPI_VISITE", List.of(
                    "#", "Tipo_di_Visita", "Categoria", "Durata_(min)",
                    "Costo_(€)", "Note"));

    /** Indica da dove prendere il valore di una colonna DB: da Excel o da un default fisso. */
    private static class ColumnMapping {
        final String dbColumn;
        final String sourceExcelColumn; // null => usa defaultValue
        final Object defaultValue;

        ColumnMapping(String dbColumn, String sourceExcelColumn, Object defaultValue) {
            this.dbColumn = dbColumn;
            this.sourceExcelColumn = sourceExcelColumn;
            this.defaultValue = defaultValue;
        }

        static ColumnMapping from(String dbColumn, String excelColumn) {
            return new ColumnMapping(dbColumn, excelColumn, null);
        }

        static ColumnMapping fixed(String dbColumn, Object defaultValue) {
            return new ColumnMapping(dbColumn, null, defaultValue);
        }
    }

    /** Mapping colonne Excel -> colonne reali del DB, per ogni tabella. */
    private static final Map<String, List<ColumnMapping>> TARGET_MAPPINGS = Map.of(

            "PROPRIETARI", List.of(
                    ColumnMapping.from("ID", "ID"),
                    ColumnMapping.from("Nome", "Nome"),
                    ColumnMapping.from("Cognome", "Cognome"),
                    ColumnMapping.from("Email", "Email"),
                    ColumnMapping.fixed("Password", null), // da cifrare prima dell'inserimento
                    ColumnMapping.from("Telefono", "Telefono"),
                    ColumnMapping.from("Indirizzo", "Indirizzo"),
                    ColumnMapping.from("Citta", "Città"), // la a accentata in "Città" potrebbe dare problemi, ma il mapping la gestisce 
                    ColumnMapping.fixed("CodiceFiscale", null), // non presente in Excel, da aggiungere
                    ColumnMapping.from("Data_Registrazione", "Data_Registrazione"),
                    ColumnMapping.fixed("IDAzienda", null), // non presente in Excel, da aggiungere
                    ColumnMapping.fixed("ID_Ruolo", null), // non presente in Excel, da aggiungere
                    ColumnMapping.fixed("isDeleted", false)), // valore default = false
                    // "Riferimento" scartata, nessuna colonna DB corrispondente

            "ANIMALI", List.of(
                    ColumnMapping.from("ID", "ID"),
                    ColumnMapping.from("Nome", "Nome"),
                    ColumnMapping.from("Specie", "Specie"),
                    ColumnMapping.from("Razza", "Razza"),
                    ColumnMapping.from("Sesso", "Sesso"),
                    ColumnMapping.from("Data_Nascita", "Data_Nascita"),
                    ColumnMapping.from("Peso", "Peso_(kg)"),
                    ColumnMapping.from("Microchip", "Microchip"),
                    ColumnMapping.from("Note", "Note_Generali"),
                    ColumnMapping.fixed("ID_Utente", null), // non presente in Excel, da aggiungere
                    ColumnMapping.fixed("isDeleted", false)),

            "VACCINAZIONI", List.of(
                    ColumnMapping.from("ID", "ID"),
                    ColumnMapping.fixed("ID_Tipo_Vaccino", null), // non presente in Excel, da aggiungere
                    ColumnMapping.from("Data_Vaccinazione", "Data_Somministrazione"),
                    ColumnMapping.from("Lotto", "Lotto"),
                    ColumnMapping.from("ID_Animale", "ID_Animale"),
                    ColumnMapping.fixed("isDeleted", false)),
                    // "Data_Scadenza" e "Stato" scartate

            "VISITE", List.of(
                    ColumnMapping.from("ID", "ID"),
                    ColumnMapping.from("ID_Animale", "ID_Animale"),
                    ColumnMapping.from("Data_Visita", "Data_Visita"),
                    ColumnMapping.fixed("ID_Tipo", null), // non presente in Excel, da aggiungere
                    ColumnMapping.fixed("ID_Dottore", null), // non presente in Excel, da aggiungere
                    ColumnMapping.fixed("ID_Pagamento", null), // non presente in Excel, da aggiungere
                    ColumnMapping.from("Note", "Note"),
                    ColumnMapping.fixed("Nota_Privata", null), // non presente in Excel, da aggiungere
                    ColumnMapping.fixed("Stato", "PRENOTATA"), // default fisso
                    ColumnMapping.fixed("isDeleted", false)),
                    // "Tipo_Visita", "Motivo_/_Diagnosi", "Trattamento", "Importo_(€)" scartate

            "TIPI_VISITE", List.of(
                    ColumnMapping.from("ID", "#"),
                    ColumnMapping.from("Nome", "Tipo_di_Visita"),
                    ColumnMapping.from("Durata", "Durata_(min)"),
                    ColumnMapping.fixed("ID_Categoria", null), // non presente in Excel, da aggiungere
                    ColumnMapping.from("Prezzo", "Costo_(€)"),
                    ColumnMapping.fixed("attivo", true),
                    ColumnMapping.fixed("ID_Dottore", null), // non presente in Excel, da aggiungere
                    ColumnMapping.fixed("isDeleted", false)));

    // ------------------------------------------------------------------
    // Flusso principale
    // ------------------------------------------------------------------

    /** Elabora tutti i fogli del workbook e concatena gli INSERT generati. */
    private static String generateSql(String excelFilePath) throws IOException {

        StringBuilder sql = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(excelFilePath);
                Workbook workbook = new XSSFWorkbook(fis)) {

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                processSheet(workbook.getSheetAt(i), sql);
            }
        }

        return sql.toString();
    }

    /** Elabora un singolo foglio: intestazione -> match tabella -> mapping -> INSERT. */
    private static void processSheet(Sheet sheet, StringBuilder sql) {

        DataFormatter formatter = new DataFormatter();
        String sheetName = sheet.getSheetName().trim();

        Iterator<Row> rows = sheet.iterator();

        if (!rows.hasNext()) {
            return;
        }

        List<String> columnNames = readHeader(rows, formatter);

        if (columnNames.isEmpty()) {
            System.out.println("Skipping sheet '" + sheetName + "' (no header found)");
            return;
        }

        String tableName = findMatchingTable(columnNames);

        if (tableName == null) {
            System.out.println("Skipping sheet '" + sheetName + "' (columns don't match any known table)");
            return;
        }

        List<ColumnMapping> mapping = TARGET_MAPPINGS.get(tableName);

        if (mapping == null) {
            System.out.println("Skipping sheet '" + sheetName + "' (no target mapping defined for " + tableName + ")");
            return;
        }

        List<List<Object>> excelData = readDataRows(rows, columnNames.size());

        List<String> targetColumns = mapping.stream().map(m -> m.dbColumn).toList();
        List<List<Object>> targetData = buildTargetRows(excelData, columnNames, mapping);

        appendInsertStatements(sql, tableName, wrapQuoted(targetColumns), targetData);

        sql.append("\n");
    }

    /** Trova la tabella il cui schema combacia con le colonne date, o null se nessuna combacia. */
    private static String findMatchingTable(List<String> columnNames) {

        for (Map.Entry<String, List<String>> entry : TABLE_SCHEMAS.entrySet()) {
            if (headerMatches(columnNames, entry.getValue())) {
                return entry.getKey();
            }
        }

        return null;
    }

    /** Confronta le colonne Excel con quelle attese (stesso numero, stessi nomi, case-insensitive). */
    private static boolean headerMatches(List<String> excelColumns,
            List<String> expectedColumns) {

        if (excelColumns.size() != expectedColumns.size()) {
            return false;
        }

        for (int i = 0; i < expectedColumns.size(); i++) {

            String excelName = excelColumns.get(i).replace("\"", "");

            if (!excelName.equalsIgnoreCase(expectedColumns.get(i))) {
                return false;
            }
        }

        return true;
    }

    // ------------------------------------------------------------------
    // Rimappatura colonne
    // ------------------------------------------------------------------

    /** Costruisce le righe DB-ready applicando il mapping a ogni riga Excel. */
    private static List<List<Object>> buildTargetRows(
            List<List<Object>> excelData,
            List<String> excelColumns,
            List<ColumnMapping> mapping) {

        List<List<Object>> result = new ArrayList<>();

        for (List<Object> excelRow : excelData) {

            List<Object> targetRow = new ArrayList<>();

            for (ColumnMapping cm : mapping) {

                if (cm.sourceExcelColumn == null) {
                    targetRow.add(cm.defaultValue);
                    continue;
                }

                String quotedKey = "\"" + cm.sourceExcelColumn + "\"";
                int idx = excelColumns.indexOf(quotedKey);

                targetRow.add(idx >= 0 ? excelRow.get(idx) : null);
            }

            result.add(targetRow);
        }

        return result;
    }

    /** Racchiude i nomi tra virgolette doppie per un uso sicuro come identificatori SQL. */
    private static List<String> wrapQuoted(List<String> names) {
        return names.stream().map(n -> "\"" + n + "\"").toList();
    }

    // ------------------------------------------------------------------
    // Lettura Excel
    // ------------------------------------------------------------------

    /** Trova la riga di intestazione (prima cella "#" o "id") e ne legge le colonne. */
    private static List<String> readHeader(Iterator<Row> rows, DataFormatter formatter) {

        List<String> columns = new ArrayList<>();

        while (rows.hasNext()) {

            Row row = rows.next();

            Cell firstCell = row.getCell(0);
            String value = formatter.formatCellValue(firstCell);

            if (value == null ||
                    (!value.equals("#") && !value.equalsIgnoreCase("id"))) {
                continue;
            }

            for (Cell cell : row) {
                String cellValue = formatter.formatCellValue(cell).trim();
                if (cellValue.isEmpty()) {
                    continue; // ignora celle vuote a fine riga
                }
                columns.add("\"" + sanitizeColumnName(cellValue) + "\"");
            }

            break;
        }

        return columns;
    }

    /** Legge le righe di dati finché non trova una riga vuota. */
    private static List<List<Object>> readDataRows(Iterator<Row> rows, int columnCount) {

        List<List<Object>> data = new ArrayList<>();

        while (rows.hasNext()) {

            Row row = rows.next();

            if (row.getPhysicalNumberOfCells() == 0) {
                break;
            }

            List<Object> values = new ArrayList<>();

            for (int c = 0; c < columnCount; c++) {
                Cell cell = row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                values.add(getCellValue(cell));
            }

            data.add(values);
        }

        return data;
    }

    /** Converte una cella nel tipo Java corrispondente. */
    private static Object getCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                double num = cell.getNumericCellValue();
                if (num == Math.floor(num) && !Double.isInfinite(num)) {
                    return (long) num;
                }
                return num;
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (Exception e) {
                    try {
                        return cell.getStringCellValue();
                    } catch (Exception e2) {
                        return null;
                    }
                }
            case BLANK:
            default:
                return null;
        }
    }

    // ------------------------------------------------------------------
    // Generazione SQL
    // ------------------------------------------------------------------

    /** Genera un INSERT per ogni riga. */
    private static void appendInsertStatements(
            StringBuilder sql,
            String tableName,
            List<String> columns,
            List<List<Object>> rows) {

        for (List<Object> row : rows) {

            sql.append("INSERT INTO ")
                    .append(tableName)
                    .append(" (")
                    .append(String.join(", ", columns))
                    .append(") VALUES (");

            for (int i = 0; i < row.size(); i++) {
                sql.append(formatValue(row.get(i)));
                if (i < row.size() - 1) {
                    sql.append(", ");
                }
            }

            sql.append(");\n");
        }
    }

    /** Converte un valore Java nel suo letterale SQL. */
    private static String formatValue(Object value) {
        if (value == null) {
            return "NULL";
        } else if (value instanceof String) {
            return "'" + ((String) value).replace("'", "''") + "'";
        } else if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return "'" + sdf.format((Date) value) + "'";
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? "1" : "0";
        } else {
            return value.toString();
        }
    }

    /** Sanifica un nome di colonna: trim + spazi sostituiti da underscore. */
    private static String sanitizeColumnName(String name) {
        return name.trim().replaceAll(" ", "_");
    }
}