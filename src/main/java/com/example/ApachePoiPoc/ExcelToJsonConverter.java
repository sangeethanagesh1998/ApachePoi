package com.example.ApachePoiPoc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelToJsonConverter {
    /**
     * Reads an Excel file and converts it to JSON.
     * @param excelFilePath Path to the Excel file (.xls or .xlsx)
     * @return JSON string representation of the Excel data
     * @throws IOException if file reading fails
     */
    public static String convertExcelToJson(String excelFilePath) throws IOException {
        File file = new File(excelFilePath);

        // Validate file existence
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Invalid file path: " + excelFilePath);
        }

        // Open file stream
        try (FileInputStream fis = new FileInputStream(file);

             Workbook workbook = excelFilePath.endsWith(".xlsx") ?
                     new XSSFWorkbook(fis) : new HSSFWorkbook(fis)) {

            Map<String, List<Map<String, String>>> excelData = new LinkedHashMap<>();

            // Iterate through all sheets
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                List<Map<String, String>> sheetData = new ArrayList<>();

                Iterator<Row> rowIterator = sheet.iterator();
                List<String> headers = new ArrayList<>();

                // Read header row
                if (rowIterator.hasNext()) {
                    Row headerRow = rowIterator.next();
                    for (Cell cell : headerRow) {
                        headers.add(cell.toString().trim());
                    }
                }

                // Read data rows
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Map<String, String> rowData = new LinkedHashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        rowData.put(headers.get(j), getCellValueAsString(cell));
                    }
                    sheetData.add(rowData);
                }

                excelData.put(sheet.getSheetName(), sheetData);
            }

            // Convert to JSON
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(excelData);
        }
    }

    /**
     * Converts a cell value to a String safely.
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            case BLANK: return "";
            default: return cell.toString();
        }
    }

    public static void main(String[] args) {
        try {
            String excelPath = "sample.xlsx"; // Change to your file path
            String jsonOutput = convertExcelToJson(excelPath);
            System.out.println(jsonOutput);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}
