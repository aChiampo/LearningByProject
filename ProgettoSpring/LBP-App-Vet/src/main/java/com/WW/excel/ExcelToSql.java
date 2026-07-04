package com.WW.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;

public class ExcelToSql {

    public static void main(String[] args) throws IOException {
        String inputFile = "VetPortal_DatiImportazione.xlsx";
        String outputFile = inputFile.replace(".xlsx", ".sql");

        String excelFilePath = new File("").getAbsolutePath()
                + "/DatiExcel/" + inputFile; // path to your Excel file
        String outputSqlPath = new File("").getAbsolutePath()
                + "/DatiExcel/" + outputFile; // path for generated SQL script

        StringBuilder sql = new StringBuilder();
        DataFormatter dataFormatter = new DataFormatter();

        try (FileInputStream fis = new FileInputStream(excelFilePath);
                Workbook workbook = new XSSFWorkbook(fis)) {

            int numberOfSheets = workbook.getNumberOfSheets();

            for (int s = 0; s < numberOfSheets; s++) {
                Sheet sheet = workbook.getSheetAt(s);
                String tableName = sanitizeName(sheet.getSheetName());
                List<String> columnNames = new ArrayList<>();

                Iterator<Row> rowIterator = sheet.iterator();
                if (!rowIterator.hasNext())
                    continue;

                // If the first row does not start with "#" or "id", skip it (assuming it's a
                // comment or metadata)
                while (rowIterator.hasNext()) {
                    Row firstRow = rowIterator.next();
                    Cell firstCell = firstRow.getCell(0);
                    String firstCellValue = dataFormatter.formatCellValue(firstCell);
                    if (firstCellValue == null || (!firstCellValue.equals("#")
                            && !firstCellValue.toLowerCase().equals("id"))) {
                        continue;
                    } else {
                        // First row = headers/column names
                        for (Cell cell : firstRow) {
                            String cellName = sanitizeName(cell.getStringCellValue());
                            columnNames.add("\"" + cellName + "\"");
                        }
                        break;
                    }
                }

                // Determine column types by scanning first data row
                List<CellType> columnTypes = new ArrayList<>(Collections.nCopies(columnNames.size(), CellType.BLANK));
                List<List<Object>> allRowsData = new ArrayList<>();

                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    if (row.getPhysicalNumberOfCells() == 0)
                        break;
                    List<Object> rowData = new ArrayList<>();

                    for (int c = 0; c < columnNames.size(); c++) {
                        Cell cell = row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        Object value = getCellValue(cell);
                        rowData.add(value);

                        // Infer type (upgrade only if still unknown/blank)
                        if (columnTypes.get(c) == CellType.BLANK && cell.getCellType() != CellType.BLANK) {
                            columnTypes.set(c, cell.getCellType());
                        }
                    }
                    allRowsData.add(rowData);
                }

                // Build CREATE TABLE statement
                sql.append("DROP TABLE IF EXISTS ").append(tableName).append(";\n");
                sql.append("CREATE TABLE ").append(tableName).append(" (\n");

                for (int c = 0; c < columnNames.size(); c++) {
                    String sqlType = mapToSqlType(columnTypes.get(c));
                    sql.append("    ").append(columnNames.get(c)).append(" ").append(sqlType);
                    if (c < columnNames.size() - 1)
                        sql.append(",");
                    sql.append("\n");
                }
                sql.append(");\n\n");

                // Build INSERT statements
                for (List<Object> rowData : allRowsData) {
                    sql.append("INSERT INTO ").append(tableName)
                            .append(" (").append(String.join(", ", columnNames)).append(") VALUES (");

                    for (int c = 0; c < rowData.size(); c++) {
                        sql.append(formatValue(rowData.get(c)));
                        if (c < rowData.size() - 1)
                            sql.append(", ");
                    }
                    sql.append(");\n");
                }

                sql.append("\n");
            }
        }

        // Write SQL to output file
        try (FileWriter writer = new FileWriter(outputSqlPath)) {
            writer.write(sql.toString());
        }

        System.out.println("SQL script generated: " + outputSqlPath);
    }

    // ---------- Helper Methods ----------

    private static String sanitizeName(String name) {
        return name.trim().replaceAll(" ", "_"); /* [^a-zA-Z0-9_] */
    }

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
                    return (long) num; // whole number
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

    private static String mapToSqlType(CellType type) {
        if (type == null)
            return "TEXT";
        switch (type) {
            case NUMERIC:
                return "DOUBLE";
            case BOOLEAN:
                return "BOOLEAN";
            case STRING:
            default:
                return "VARCHAR(255)";
        }
    }

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

}