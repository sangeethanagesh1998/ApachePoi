package com.example.ApachePoiPoc.service;

import com.example.ApachePoiPoc.exception.ExcelParsingException;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Service layer for Excel file processing operations
 */
@Service
public class ExcelService {
    private static final Logger logger = LoggerFactory.getLogger(ExcelService.class);

    public List<Map<String, Object>> parseExcel(MultipartFile file) throws IOException {

        logger.info("Starting to parse Excel file: {}", file.getOriginalFilename());

        if (file == null || file.isEmpty()) {
            logger.error("Uploaded file is empty");
            throw new ExcelParsingException(
                    "File is required and cannot be empty");
        }

        logger.info("File received: {}", file.getOriginalFilename());

        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        if (contentType == null ||
                (!contentType.equals(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") ||
                        originalFilename == null ||
                        !originalFilename.toLowerCase().endsWith(".xlsx"))) {

            logger.error("Invalid file format: {}", originalFilename);

            throw new ExcelParsingException(
                    "Only Excel files with .xlsx extension are allowed");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            logger.info("Workbook opened successfully");

            List<Map<String, Object>> dataList = new ArrayList<>();

            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                logger.error("The Excel file is empty or does not contain a header row.");
                throw new ExcelParsingException("Excel file must have a header row");
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Map<String, Object> rowData = new LinkedHashMap<>();
                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = row.getCell(j);
                        rowData.put(headers.get(j), getCellValue(cell));
                    }
                    dataList.add(rowData);
                }
            }

            logger.info("Excel parsing completed. Total rows parsed: {}", dataList.size());

            logger.info("**************************************");
            return dataList;
        } catch (NotOfficeXmlFileException e) {
            logger.error("Invalid Excel file content", e);
            throw new ExcelParsingException("The uploaded file is not a valid Excel file");
        } catch (ExcelParsingException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during Excel parsing", e);
            throw new ExcelParsingException("Failed to parse Excel file");
        }
    }

    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
