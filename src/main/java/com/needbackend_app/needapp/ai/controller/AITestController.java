package com.needbackend_app.needapp.ai.controller;

import com.needbackend_app.needapp.ai.dto.CategoryRecommendationRequest;
import com.needbackend_app.needapp.ai.dto.CategoryRecommendationResponse;
import com.needbackend_app.needapp.ai.service.AIAssistantService;
import com.needbackend_app.needapp.ai.service.AIResponseMonitor;
import com.needbackend_app.needapp.user.customer.dto.CategoryDocumentDTO;
import com.needbackend_app.needapp.user.customer.service.CategoryValidationService;
import com.needbackend_app.needapp.user.customer.service.SuggestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai/test")
@RequiredArgsConstructor
@Tag(name = "AI Testing", description = "Endpoints for testing and debugging AI functionality")
public class AITestController {

    private final AIAssistantService aiAssistantService;
    private final SuggestService suggestService;
    private final CategoryValidationService categoryValidationService;
    private final AIResponseMonitor responseMonitor;

    @PostMapping("/category-recommendation")
    @Operation(summary = "Test AI category recommendation directly")
    public ResponseEntity<CategoryRecommendationResponse> testCategoryRecommendation(
            @RequestBody CategoryRecommendationRequest request) {
        CategoryRecommendationResponse response = aiAssistantService.recommendCategories(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggest-categories")
    @Operation(summary = "Test category suggestion with AI fallback")
    public ResponseEntity<List<CategoryDocumentDTO>> testCategorySuggestion(
            @RequestParam String query) {
        List<CategoryDocumentDTO> suggestions = suggestService.suggestCategories(query);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/validate-category")
    @Operation(summary = "Test category validation")
    public ResponseEntity<Map<String, Object>> testCategoryValidation(
            @RequestParam String categoryName) {
        
        boolean isValid = categoryValidationService.isValidCategoryName(categoryName);
        boolean isInvalid = categoryValidationService.isInvalidResponse(categoryName);
        var bestMatch = categoryValidationService.findBestMatch(categoryName);
        var defaultCategory = categoryValidationService.getDefaultCategory();
        
        Map<String, Object> result = Map.of(
            "categoryName", categoryName,
            "isValid", isValid,
            "isInvalidResponse", isInvalid,
            "bestMatch", bestMatch.map(cat -> Map.of("id", cat.getId(), "name", cat.getName())).orElse(null),
            "defaultCategory", defaultCategory.map(cat -> Map.of("id", cat.getId(), "name", cat.getName())).orElse(null)
        );
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/response-stats")
    @Operation(summary = "Get AI response monitoring statistics")
    public ResponseEntity<Map<String, Object>> getResponseStats() {
        Map<String, Integer> invalidStats = responseMonitor.getAllInvalidResponseStats();
        
        Map<String, Object> stats = Map.of(
            "invalidResponseCounts", invalidStats,
            "totalInvalidResponses", invalidStats.values().stream().mapToInt(Integer::intValue).sum(),
            "uniqueInvalidResponses", invalidStats.size()
        );
        
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/clear-stats")
    @Operation(summary = "Clear AI response monitoring statistics")
    public ResponseEntity<Map<String, String>> clearStats() {
        responseMonitor.clearStats();
        return ResponseEntity.ok(Map.of("message", "Statistics cleared successfully"));
    }

    @PostMapping("/simulate-invalid-response")
    @Operation(summary = "Simulate an invalid AI response for testing")
    public ResponseEntity<Map<String, String>> simulateInvalidResponse(
            @RequestParam String response) {
        responseMonitor.recordInvalidResponse(response);
        return ResponseEntity.ok(Map.of(
            "message", "Recorded invalid response: " + response,
            "count", String.valueOf(responseMonitor.getInvalidResponseCount(response))
        ));
    }
}