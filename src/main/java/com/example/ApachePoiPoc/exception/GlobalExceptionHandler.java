package com.example.ApachePoiPoc.exception;

import com.example.ApachePoiPoc.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler for centralized error handling across the application
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle FileProcessingException
     */
    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ApiResponse<Object>> handleFileProcessingException(
            FileProcessingException ex,
            WebRequest request) {

        logger.error("FileProcessingException occurred: {} [Code: {}]", ex.getMessage(), ex.getErrorCode());

        ApiResponse<Object> response = new ApiResponse<>(
                false,
                ex.getMessage(),
                ex.getErrorCode()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        logger.error("IllegalArgumentException occurred: {}", ex.getMessage());

        ApiResponse<Object> response = new ApiResponse<>(
                false,
                ex.getMessage(),
                "INVALID_ARGUMENT"
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle IllegalStateException
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(
            IllegalStateException ex,
            WebRequest request) {

        logger.error("IllegalStateException occurred: {}", ex.getMessage());

        ApiResponse<Object> response = new ApiResponse<>(
                false,
                ex.getMessage(),
                "INVALID_STATE"
        );

        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Handle generic Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex,
            WebRequest request) {

        logger.error("Unexpected exception occurred: {}", ex.getMessage(), ex);

        ApiResponse<Object> response = new ApiResponse<>(
                false,
                "An unexpected error occurred. Please try again later.",
                "INTERNAL_SERVER_ERROR"
        );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

