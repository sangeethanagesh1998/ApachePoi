package com.example.ApachePoiPoc;

import com.example.ApachePoiPoc.dto.ApiResponse;
import com.example.ApachePoiPoc.service.ExcelProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * REST Controller for Excel file upload operations
 */
@RestController
@RequestMapping("api/excel")
public class ExcelUploadController {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUploadController.class);

    @Autowired
    private ExcelProcessingService excelProcessingService;

    /**
     * Upload and process a large Excel file
     *
     * @param file the Excel file to upload and process
     * @return ResponseEntity with ApiResponse containing processing results
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadLargeExcel(
            @RequestParam("file") MultipartFile file) {

        logger.info("Received file upload request for: {}", file.getOriginalFilename());

        try {
            // Process the file using the service layer
            Map<String, Object> result = excelProcessingService.processExcelFile(file);

            int rowCount = (Integer) result.get("rowCount");
            String message = String.format("Processed %d rows successfully from sheet: %s",
                    rowCount, result.get("sheetName"));

            logger.info("File upload successful: {}", message);

            ApiResponse<Map<String, Object>> response = ApiResponse.success(message, result);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error during file upload: {}", e.getMessage());
            // GlobalExceptionHandler will handle the exception
            throw e;
        }
    }

}
