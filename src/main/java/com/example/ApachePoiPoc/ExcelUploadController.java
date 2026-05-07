package com.example.ApachePoiPoc;

import com.github.pjfanning.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("api/excel")
public class ExcelUploadController {


    @PostMapping("/upload")
    public ResponseEntity<String> uploadLargeExcel(@RequestParam("file") MultipartFile file) {


        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        // Validate file type
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.badRequest().body("Only .xlsx files are supported");
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = StreamingReader.builder()
                     .rowCacheSize(100)     // Number of rows to keep in memory
                     .bufferSize(4096)      // Stream buffer size
                     .open(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            int rowCount = 0;

            for (Row row : sheet) {
                StringBuilder rowData = new StringBuilder();
                for (Cell cell : row) {
                    rowData.append(getCellValue(cell)).append(" | ");
                }
                // Here you can save to DB or process the row
                System.out.println("Row " + rowCount + ": " + rowData);
                rowCount++;
            }

            return ResponseEntity.ok("Processed " + rowCount + " rows successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    // Helper method to safely get cell values
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
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
