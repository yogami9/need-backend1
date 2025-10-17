package com.needbackend_app.needapp.ai.exception;

public class AIException extends RuntimeException {
    public AIException(String message) {
        super(message);
    }
    
    public AIException(String message, Throwable cause) {
        super(message, cause);
    }
}