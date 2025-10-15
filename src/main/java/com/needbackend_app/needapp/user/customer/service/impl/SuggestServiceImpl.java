package com.needbackend_app.needapp.user.customer.service.impl;

import com.needbackend_app.needapp.ai.dto.CategoryRecommendationRequest;
import com.needbackend_app.needapp.ai.dto.CategoryRecommendationResponse;
import com.needbackend_app.needapp.ai.service.AIAssistantService;
import com.needbackend_app.needapp.user.customer.dto.CategoryDocumentDTO;
import com.needbackend_app.needapp.user.customer.dto.SubCategoryDocumentDTO;
import com.needbackend_app.needapp.user.customer.dto.SuggestionResponseDTO;
import com.needbackend_app.needapp.user.customer.model.Category;
import com.needbackend_app.needapp.user.customer.model.SubCategory;
import com.needbackend_app.needapp.user.customer.repository.CategoryRepository;
import com.needbackend_app.needapp.user.customer.repository.SubCategoryRepository;
import com.needbackend_app.needapp.user.customer.service.CustomCategorySearch;
import com.needbackend_app.needapp.user.customer.service.CustomSubCategorySearch;
import com.needbackend_app.needapp.user.customer.service.SuggestService;
import com.needbackend_app.needapp.user.util.QueryPreprocessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuggestServiceImpl implements SuggestService {
    
    private final CustomCategorySearch customCategorySearch;
    private final CustomSubCategorySearch customSubCategorySearch;
    private final QueryPreprocessor preprocessor;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    
    @Autowired(required = false)
    private AIAssistantService aiAssistantService;
    
    // Track invalid AI responses for monitoring
    private final Map<String, AtomicInteger> invalidResponseCounts = new ConcurrentHashMap<>();

    @Override
    public List<CategoryDocumentDTO> suggestCategories(String query) {
        String cleanedQuery = preprocessor.preprocessor(query);
        List<CategoryDocumentDTO> results = customCategorySearch.searchByText(cleanedQuery);
        
        // If no results and AI is available, try AI fallback
        if (results.isEmpty() && aiAssistantService != null) {
            results = getAIFallbackCategories(query);
        }
        
        return results;
    }

    @Override
    public List<SubCategoryDocumentDTO> suggestSubCategories(String query, UUID categoryId) {
        String cleanedQuery = preprocessor.preprocessor(query);
        List<SubCategoryDocumentDTO> results = customSubCategorySearch.searchByTextAndCategoryId(cleanedQuery, categoryId);
        
        // If no results and AI is available, try AI fallback
        if (results.isEmpty() && aiAssistantService != null) {
            results = getAIFallbackSubCategories(query, categoryId);
        }
        
        return results;
    }

    @Override
    public SuggestionResponseDTO suggest(String query, UUID categoryId) {
        String cleanedQuery = preprocessor.preprocessor(query);
        List<CategoryDocumentDTO> categories = new ArrayList<>();
        List<SubCategoryDocumentDTO> subcategories = new ArrayList<>();

        if (categoryId == null) {
            // Search for categories
            categories = customCategorySearch.searchByText(cleanedQuery);
            
            // AI fallback for categories
            if (categories.isEmpty() && aiAssistantService != null) {
                categories = getAIFallbackCategories(query);
            }
        } else {
            // Search for subcategories within specified category
            subcategories = customSubCategorySearch.searchByTextAndCategoryId(cleanedQuery, categoryId);
            
            // AI fallback for subcategories
            if (subcategories.isEmpty() && aiAssistantService != null) {
                subcategories = getAIFallbackSubCategories(query, categoryId);
            }
        }

        return new SuggestionResponseDTO(categories, subcategories);
    }

    private List<CategoryDocumentDTO> getAIFallbackCategories(String query) {
        try {
            CategoryRecommendationResponse aiSuggestion = aiAssistantService.recommendCategories(
                new CategoryRecommendationRequest(query)
            );
            
            if (aiSuggestion.success() && aiSuggestion.recommendedCategory() != null) {
                String suggestedCategory = aiSuggestion.recommendedCategory().trim();
                
                // Check for invalid AI responses
                if (isInvalidAIResponse(suggestedCategory)) {
                    recordInvalidResponse(suggestedCategory);
                    log.warn("AI returned invalid category suggestion: '{}', using default fallback", suggestedCategory);
                    return getDefaultCategoryFallback();
                }
                
                // Try exact match first
                Optional<Category> exactMatch = categoryRepository.findByNameIgnoreCase(suggestedCategory);
                if (exactMatch.isPresent()) {
                    Category cat = exactMatch.get();
                    log.info("AI fallback found exact matching category: '{}'", cat.getName());
                    return List.of(new CategoryDocumentDTO(cat.getId(), cat.getName(), cat.getDescription()));
                }
                
                // Try fuzzy matching for partial matches
                Optional<Category> fuzzyMatch = findFuzzyMatch(suggestedCategory);
                if (fuzzyMatch.isPresent()) {
                    Category cat = fuzzyMatch.get();
                    log.info("AI fallback found fuzzy matching category: '{}' for suggestion: '{}'", 
                        cat.getName(), suggestedCategory);
                    return List.of(new CategoryDocumentDTO(cat.getId(), cat.getName(), cat.getDescription()));
                }
                
                log.warn("AI suggested category '{}' not found in database", suggestedCategory);
                recordInvalidResponse(suggestedCategory);
            } else {
                log.warn("AI category recommendation failed or returned empty result");
            }
        } catch (Exception e) {
            log.warn("AI fallback for categories failed: {}", e.getMessage());
        }
        
        return getDefaultCategoryFallback();
    }

    private List<SubCategoryDocumentDTO> getAIFallbackSubCategories(String query, UUID categoryId) {
        try {
            CategoryRecommendationResponse aiSuggestion = aiAssistantService.recommendCategories(
                new CategoryRecommendationRequest(query)
            );
            
            if (aiSuggestion.success() && aiSuggestion.recommendedSubCategories() != null) {
                List<SubCategoryDocumentDTO> results = new ArrayList<>();
                
                // Validate each suggested subcategory
                for (String subCatName : aiSuggestion.recommendedSubCategories()) {
                    if (isInvalidAIResponse(subCatName)) {
                        recordInvalidResponse(subCatName);
                        continue;
                    }
                    
                    // Try to find AI-suggested subcategories in our database
                    List<SubCategory> foundSubCategories = subCategoryRepository.findByNameInIgnoreCase(
                        List.of(subCatName.trim())
                    );
                    
                    for (SubCategory subCat : foundSubCategories) {
                        // Only include subcategories that belong to the specified category
                        if (subCat.getCategory().getId().equals(categoryId)) {
                            results.add(new SubCategoryDocumentDTO(
                                subCat.getId(),
                                subCat.getName(),
                                subCat.getDescription(),
                                subCat.getCategory().getId()
                            ));
                        }
                    }
                }
                
                if (!results.isEmpty()) {
                    log.info("AI fallback found {} matching subcategories for category {}", 
                        results.size(), categoryId);
                    return results;
                }
            }
        } catch (Exception e) {
            log.warn("AI fallback for subcategories failed: {}", e.getMessage());
        }
        
        return List.of(); // Return empty list if AI fallback fails
    }
    
    private boolean isInvalidAIResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return true;
        }
        
        String upper = response.trim().toUpperCase();
        Set<String> invalidResponses = Set.of(
            "N/A", "NOT AVAILABLE", "NONE", "NULL", "UNKNOWN", "OTHER", 
            "GENERAL", "MISC", "MISCELLANEOUS", "UNDEFINED", "DEFAULT"
        );
        
        return invalidResponses.contains(upper);
    }
    
    private void recordInvalidResponse(String response) {
        invalidResponseCounts.computeIfAbsent(response, k -> new AtomicInteger(0)).incrementAndGet();
        
        int count = invalidResponseCounts.get(response).get();
        if (count > 5) {
            log.error("AI frequently returning invalid response '{}': {} times", response, count);
        }
    }
    
    private Optional<Category> findFuzzyMatch(String suggestedCategory) {
        List<Category> allCategories = categoryRepository.findAll();
        String searchLower = suggestedCategory.toLowerCase();
        
        // Try different fuzzy matching strategies
        for (Category cat : allCategories) {
            String catLower = cat.getName().toLowerCase();
            
            // Contains match
            if (catLower.contains(searchLower) || searchLower.contains(catLower)) {
                return Optional.of(cat);
            }
            
            // Word-level matching
            if (hasWordMatch(catLower, searchLower)) {
                return Optional.of(cat);
            }
        }
        
        return Optional.empty();
    }
    
    private boolean hasWordMatch(String categoryName, String searchTerm) {
        String[] catWords = categoryName.split("\\s+");
        String[] searchWords = searchTerm.split("\\s+");
        
        for (String catWord : catWords) {
            for (String searchWord : searchWords) {
                if (catWord.equals(searchWord) || 
                    catWord.contains(searchWord) || 
                    searchWord.contains(catWord)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private List<CategoryDocumentDTO> getDefaultCategoryFallback() {
        try {
            // Try to find a general category first
            List<Category> allCategories = categoryRepository.findAll();
            
            for (Category category : allCategories) {
                String name = category.getName().toLowerCase();
                if (name.contains("cleaning") || name.contains("maintenance") || 
                    name.contains("general") || name.contains("other")) {
                    return List.of(new CategoryDocumentDTO(
                        category.getId(), category.getName(), category.getDescription()
                    ));
                }
            }
            
            // If no suitable category found, return the first available category
            if (!allCategories.isEmpty()) {
                Category cat = allCategories.get(0);
                log.info("Using first available category as fallback: '{}'", cat.getName());
                return List.of(new CategoryDocumentDTO(cat.getId(), cat.getName(), cat.getDescription()));
            }
            
        } catch (Exception e) {
            log.error("Error getting default category fallback: {}", e.getMessage());
        }
        
        return List.of(); // Return empty list if all else fails
    }
    
    // Scheduled method to log invalid response statistics (add @Scheduled annotation if needed)
    public void logInvalidResponseStats() {
        if (!invalidResponseCounts.isEmpty()) {
            log.info("AI Invalid Response Stats: {}", invalidResponseCounts);
        }
    }
}