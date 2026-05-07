package com.example.ApachePoiPoc.exception;

/**
 * Custom exception for file processing errors
 */
public class FileProcessingException extends RuntimeException {
    private String errorCode;

    public FileProcessingException(String message) {
        super(message);
        this.errorCode = "FILE_PROCESSING_ERROR";
    }

    public FileProcessingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FILE_PROCESSING_ERROR";
    }

    public FileProcessingException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

