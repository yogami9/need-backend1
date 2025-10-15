package com.needbackend_app.needapp.ai.exception;

import com.needbackend_app.needapp.ai.dto.AIResponse;
import com.needbackend_app.needapp.user.util.ApiErrorResponse;
import com.needbackend_app.needapp.user.util.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class AIExceptionHandler {

    @ExceptionHandler(AIException.class)
    public ResponseEntity<AIResponse> handleAIException(AIException ex) {
        log.error("AI service error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new AIResponse(false, ex.getMessage(), null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericAIError(Exception ex) {
        log.error("Unexpected error in AI service: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(false, null, "AI service temporarily unavailable", ErrorType.ERROR));
    }
}