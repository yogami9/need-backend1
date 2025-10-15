package com.needbackend_app.needapp.ai.service;

import com.needbackend_app.needapp.ai.config.AIProperties;
import com.needbackend_app.needapp.ai.dto.AIRequest;
import com.needbackend_app.needapp.ai.dto.AIResponse;
import com.needbackend_app.needapp.ai.dto.CategoryRecommendationRequest;
import com.needbackend_app.needapp.ai.dto.CategoryRecommendationResponse;
import com.needbackend_app.needapp.user.customer.model.Category;
import com.needbackend_app.needapp.user.customer.model.SubCategory;
import com.needbackend_app.needapp.user.customer.repository.CategoryRepository;
import com.needbackend_app.needapp.user.customer.repository.SubCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIAssistantService {

    private final HuggingFaceService huggingFaceService;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final AIProperties aiProperties;

    public CategoryRecommendationResponse recommendCategories(CategoryRecommendationRequest request) {
        if (!aiProperties.getFeatures().isCategoryRecommendation()) {
            return new CategoryRecommendationResponse(false, "Category recommendation is disabled", null, null);
        }

        try {
            List<Category> availableCategories = categoryRepository.findAll();
            String categoriesContext = buildCategoriesContext(availableCategories);
            
            String prompt = String.format("""
                Based on the following service description, recommend the most suitable categories and subcategories:
                
                Description: "%s"
                
                Available Categories:
                %s
                
                IMPORTANT RULES:
                1. You MUST choose from the available categories listed above
                2. Use exact category names as shown in the list
                3. If no category fits perfectly, choose the closest match
                4. Never respond with 'N/A', 'Not Available', 'None', or similar
                5. Always provide at least one category from the list
                6. For subcategories, only suggest ones that exist under the chosen category
                
                Please respond with:
                Category: [exact category name from the list above]
                Subcategories: [subcategory1, subcategory2]
                Explanation: [brief explanation of why this category fits]
                """, request.serviceDescription(), categoriesContext);

            AIResponse aiResponse = huggingFaceService.generateCategoryRecommendation(prompt);
            
            if (aiResponse.success()) {
                return parseRecommendationResponse(aiResponse.content(), availableCategories);
            } else {
                return new CategoryRecommendationResponse(false, aiResponse.message(), null, null);
            }
            
        } catch (Exception e) {
            log.error("Error in category recommendation: {}", e.getMessage(), e);
            return new CategoryRecommendationResponse(false, "Failed to generate recommendations", null, null);
        }
    }

    public AIResponse generateServiceDescription(String category, List<String> subCategories, String userInput) {
        if (!aiProperties.getFeatures().isServiceDescriptionGeneration()) {
            return new AIResponse(false, "Service description generation is disabled", null);
        }

        String prompt = String.format("""
            Generate a professional service description for a service provider in the following category:
            
            Category: %s
            Subcategories: %s
            Additional details: %s
            
            The description should be:
            - Professional and engaging
            - 2-3 sentences long
            - Highlight key benefits and expertise
            - Be specific to the category and subcategories
            """, category, String.join(", ", subCategories), userInput);

        return huggingFaceService.generateServiceDescription(prompt);
    }

    public AIResponse moderateContent(String content) {
        if (!aiProperties.getFeatures().isContentModeration()) {
            return new AIResponse(true, "Content moderation is disabled", "APPROVED");
        }

        String prompt = String.format("""
            Review the following content for appropriateness in a professional service marketplace:
            
            Content: "%s"
            
            Check for:
            - Inappropriate language
            - Spam or promotional content
            - Personal information that shouldn't be shared
            - Content that violates professional standards
            
            Respond with one of: APPROVED, FLAGGED, REJECTED
            If FLAGGED or REJECTED, provide a brief reason.
            
            Format: STATUS: [your decision]
            Reason: [if applicable]
            """, content);

        return huggingFaceService.moderateContent(prompt);
    }

    public AIResponse provideChatSupport(String userMessage, String context) {
        if (!aiProperties.getFeatures().isChatSupport()) {
            return new AIResponse(false, "Chat support is disabled", null);
        }

        AIRequest request = new AIRequest(
            userMessage,
            "You are a helpful assistant for a service marketplace app called NEED. Help users with their questions about finding services, using the platform, or general support.",
            context,
            null,
            List.of("Be helpful and professional", "Keep responses concise", "Direct users to human support for complex issues")
        );

        return huggingFaceService.generateContentWithContext(request);
    }

    private String buildCategoriesContext(List<Category> categories) {
        return categories.stream()
                .map(category -> {
                    String subcats = category.getSubCategories().stream()
                            .map(SubCategory::getName)
                            .collect(Collectors.joining(", "));
                    return String.format("- %s: [%s]", category.getName(), subcats);
                })
                .collect(Collectors.joining("\n"));
    }

    private CategoryRecommendationResponse parseRecommendationResponse(String aiResponse, List<Category> availableCategories) {
        try {
            String[] lines = aiResponse.split("\n");
            String category = null;
            String subcategories = null;
            String explanation = null;

            for (String line : lines) {
                line = line.trim();
                if (line.toLowerCase().startsWith("category:")) {
                    category = line.substring(9).trim();
                } else if (line.toLowerCase().startsWith("subcategories:")) {
                    subcategories = line.substring(14).trim();
                } else if (line.toLowerCase().startsWith("explanation:")) {
                    explanation = line.substring(12).trim();
                }
            }

            // Validate the suggested category exists
            if (category != null) {
                category = validateAndCorrectCategory(category, availableCategories);
            }

            // If still no valid category, use fallback
            if (category == null || isInvalidCategory(category)) {
                log.warn("AI returned invalid category '{}', using fallback", category);
                category = getDefaultCategoryName(availableCategories);
                subcategories = "";
            }

            List<String> subcategoryList = subcategories != null && !subcategories.isEmpty() ? 
                List.of(subcategories.split(",")).stream()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList()) : List.of();

            return new CategoryRecommendationResponse(true, "Recommendations generated", category, subcategoryList);
            
        } catch (Exception e) {
            log.error("Error parsing AI recommendation response: {}", e.getMessage(), e);
            
            String fallbackCategory = getDefaultCategoryName(availableCategories);
            return new CategoryRecommendationResponse(true, "Using fallback recommendation", fallbackCategory, List.of());
        }
    }

    private String validateAndCorrectCategory(String suggestedCategory, List<Category> availableCategories) {
        if (suggestedCategory == null || suggestedCategory.trim().isEmpty()) {
            return null;
        }

        String trimmed = suggestedCategory.trim();

        // Check for exact match (case-insensitive)
        for (Category category : availableCategories) {
            if (category.getName().equalsIgnoreCase(trimmed)) {
                return category.getName();
            }
        }

        // Check for partial match
        for (Category category : availableCategories) {
            String catName = category.getName().toLowerCase();
            String suggested = trimmed.toLowerCase();
            
            if (catName.contains(suggested) || suggested.contains(catName)) {
                log.info("Found partial match for '{}': '{}'", trimmed, category.getName());
                return category.getName();
            }
        }

        return null;
    }

    private boolean isInvalidCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return true;
        }

        String upper = category.trim().toUpperCase();
        List<String> invalidResponses = List.of(
            "N/A", "NOT AVAILABLE", "NONE", "NULL", "UNKNOWN", "OTHER", "GENERAL", "MISC", "MISCELLANEOUS"
        );

        return invalidResponses.contains(upper);
    }

    private String getDefaultCategoryName(List<Category> availableCategories) {
        if (availableCategories.isEmpty()) {
            return "General";
        }

        // Try to find a general category first
        for (Category category : availableCategories) {
            String name = category.getName().toLowerCase();
            if (name.contains("general") || name.contains("other") || name.contains("misc")) {
                return category.getName();
            }
        }

        // If no general category, return the first one
        return availableCategories.get(0).getName();
    }
}