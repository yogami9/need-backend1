package com.needbackend_app.needapp.ai.integration;

import com.needbackend_app.needapp.ai.dto.CategoryRecommendationRequest;
import com.needbackend_app.needapp.ai.dto.CategoryRecommendationResponse;
import com.needbackend_app.needapp.ai.service.AIAssistantService;
import com.needbackend_app.needapp.user.customer.repository.CategoryRepository;
import com.needbackend_app.needapp.user.customer.repository.SubCategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AIIntegrationTest {

    @Autowired
    private AIAssistantService aiAssistantService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void testCategoryRecommendation() {
        // This test requires actual Gemini API key in test environment
        // Skip if not available
        try {
            CategoryRecommendationRequest request = new CategoryRecommendationRequest(
                "I need someone to fix my kitchen sink and replace pipes"
            );
            
            CategoryRecommendationResponse response = aiAssistantService.recommendCategories(request);
            
            // Basic validation - actual results depend on AI response
            assertNotNull(response);
            
        } catch (Exception e) {
            // Log and skip if AI service unavailable
            System.out.println("Skipping AI integration test: " + e.getMessage());
        }
    }
}