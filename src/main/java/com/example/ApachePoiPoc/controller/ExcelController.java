package com.example.ApachePoiPoc.controller;

import com.example.ApachePoiPoc.dto.ApiResponse;
import com.example.ApachePoiPoc.service.ExcelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {
    /**
     * Reads an Excel file and converts it to JSON.
     * @param excelFilePath Path to the Excel file (.xls or .xlsx)
     * @return JSON string representation of the Excel data
     * @throws IOException if file reading fails
     */

    private final ExcelService excelService;

    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<?>> uploadExcel(@RequestParam("file")MultipartFile file) throws IOException {
        List<Map<String, Object>> data =excelService.parseExcel(file);

        ApiResponse<?> response =
                new ApiResponse<>(
                        true,
                        "Excel file parsed successfully",
                        data);

    return ResponseEntity.ok(response);
    }
}

