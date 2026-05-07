package com.example.ApachePoiPoc.exception;

import com.example.ApachePoiPoc.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

/**
 * Global exception handler for centralized error handling across the application
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleExcelParsingException(RuntimeException ex) {

        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "success", false,
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handleMissingFileException(MissingServletRequestPartException ex) {
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "success", false,
                        "message", "Required request part 'file' is missing"
                ));
    }

}

