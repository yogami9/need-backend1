package com.needbackend_app.needapp.ai.controller;

import com.needbackend_app.needapp.ai.service.AIHealthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai/health")
@RequiredArgsConstructor
@Tag(name = "AI Health", description = "AI service health monitoring")
public class AIHealthController {

    private final AIHealthService healthService;

    @GetMapping
    @Operation(summary = "Check AI service health status")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        boolean isHealthy = healthService.isAIServiceHealthy();
        String detailedStatus = healthService.getDetailedHealthStatus();
        
        Map<String, Object> healthData = Map.of(
            "status", isHealthy ? "UP" : "DOWN",
            "details", detailedStatus,
            "timestamp", System.currentTimeMillis()
        );
        
        return ResponseEntity.ok(healthData);
    }
}
