package com.example.ApachePoiPoc.service;

import com.github.pjfanning.xlsx.StreamingReader;
import com.example.ApachePoiPoc.exception.FileProcessingException;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Service layer for Excel file processing operations
 */
@Service
public class ExcelProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelProcessingService.class);
    private static final String XLSX_EXTENSION = ".xlsx";
    private static final int ROW_CACHE_SIZE = 100;
    private static final int BUFFER_SIZE = 4096;

    /**
     * Validates and processes an uploaded Excel file
     *
     * @param file the MultipartFile to process
     * @return Map containing processing result with row count and status
     * @throws FileProcessingException if file validation or processing fails
     */
    public Map<String, Object> processExcelFile(MultipartFile file) {
        logger.info("Starting Excel file processing for file: {}", file.getOriginalFilename());

        validateFile(file);

        Map<String, Object> result = new HashMap<>();
        int rowCount = 0;

        try (InputStream is = file.getInputStream();
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(ROW_CACHE_SIZE)
                     .bufferSize(BUFFER_SIZE)
                     .open(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            String sheetName = sheet.getSheetName();
            logger.debug("Processing sheet: {}", sheetName);

            for (Row row : sheet) {
                StringBuilder rowData = new StringBuilder();
                for (Cell cell : row) {
                    rowData.append(getCellValue(cell)).append(" | ");
                }
                logger.trace("Row {}: {}", rowCount, rowData);
                rowCount++;
            }

            result.put("rowCount", rowCount);
            result.put("sheetName", sheetName);
            result.put("fileName", file.getOriginalFilename());

            logger.info("Successfully processed {} rows from file: {}", rowCount, file.getOriginalFilename());

        } catch (IOException e) {
            logger.error("IO error while processing Excel file: {}", file.getOriginalFilename(), e);
            throw new FileProcessingException(
                    "Error reading Excel file: " + e.getMessage(),
                    "IO_ERROR",
                    e
            );
        } catch (Exception e) {
            logger.error("Unexpected error while processing Excel file: {}", file.getOriginalFilename(), e);
            throw new FileProcessingException(
                    "Unexpected error processing file: " + e.getMessage(),
                    "PROCESSING_ERROR",
                    e
            );
        }

        return result;
    }

    /**
     * Validates the uploaded file
     *
     * @param file the file to validate
     * @throws FileProcessingException if validation fails
     */
    private void validateFile(MultipartFile file) {
        logger.debug("Validating file: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            logger.warn("File is empty");
            throw new FileProcessingException("File is empty", "EMPTY_FILE");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(XLSX_EXTENSION)) {
            logger.warn("Invalid file type: {}", filename);
            throw new FileProcessingException(
                    "Only .xlsx files are supported",
                    "INVALID_FILE_TYPE"
            );
        }

        logger.debug("File validation successful for: {}", filename);
    }

    /**
     * Safely extracts the value from a cell based on its type
     *
     * @param cell the cell to extract value from
     * @return string representation of the cell value
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                    ? cell.getDateCellValue().toString()
                    : String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}

