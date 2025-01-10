package com.example.cleanorarest.Exception;

public class GeoapifyProcessingException extends RuntimeException {
    public GeoapifyProcessingException(String message) {
        super(message);
    }

    public GeoapifyProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}