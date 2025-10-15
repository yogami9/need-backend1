package com.needbackend_app.needapp.ai.dto;

public record AIResponse(
    boolean success,
    String message,
    String content
) {}