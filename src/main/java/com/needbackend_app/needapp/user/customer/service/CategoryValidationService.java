package com.needbackend_app.needapp.user.customer.service;

import com.needbackend_app.needapp.user.customer.model.Category;
import com.needbackend_app.needapp.user.customer.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryValidationService {
    
    private final CategoryRepository categoryRepository;
    private final LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
    
    /**
     * Validates if a category name is valid (exists in database and is not an invalid response)
     */
    public boolean isValidCategoryName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = categoryName.trim();
        
        // Check for invalid responses
        if (isInvalidResponse(trimmed)) {
            return false;
        }
        
        return categoryRepository.findByNameIgnoreCase(trimmed).isPresent();
    }
    
    /**
     * Finds the best matching category for a given category name
     */
    public Optional<Category> findBestMatch(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String trimmed = categoryName.trim();
        
        // Skip invalid responses
        if (isInvalidResponse(trimmed)) {
            log.debug("Skipping invalid category name: '{}'", trimmed);
            return Optional.empty();
        }
        
        // Try exact match first
        Optional<Category> exact = categoryRepository.findByNameIgnoreCase(trimmed);
        if (exact.isPresent()) {
            log.debug("Found exact match for category: '{}'", trimmed);
            return exact;
        }
        
        // Try fuzzy matching
        List<Category> allCategories = categoryRepository.findAll();
        Optional<Category> bestMatch = Optional.empty();
        double bestSimilarity = 0.0;
        
        for (Category category : allCategories) {
            double similarity = calculateSimilarity(category.getName(), trimmed);
            
            if (similarity > bestSimilarity && similarity >= 0.6) { // 60% similarity threshold
                bestSimilarity = similarity;
                bestMatch = Optional.of(category);
            }
        }
        
        if (bestMatch.isPresent()) {
            log.info("Found fuzzy match for '{}': '{}' with similarity {}", 
                trimmed, bestMatch.get().getName(), String.format("%.2f", bestSimilarity));
        }
        
        return bestMatch;
    }
    
    /**
     * Gets the most appropriate default category
     */
    public Optional<Category> getDefaultCategory() {
        List<Category> allCategories = categoryRepository.findAll();
        
        // Look for common general category names
        List<String> preferredDefaults = Arrays.asList(
            "general", "other", "miscellaneous", "cleaning", "maintenance"
        );
        
        for (String preferred : preferredDefaults) {
            for (Category category : allCategories) {
                if (category.getName().toLowerCase().contains(preferred)) {
                    log.info("Using preferred default category: '{}'", category.getName());
                    return Optional.of(category);
                }
            }
        }
        
        // If no preferred default found, return the first category
        if (!allCategories.isEmpty()) {
            Category firstCategory = allCategories.get(0);
            log.info("Using first available category as default: '{}'", firstCategory.getName());
            return Optional.of(firstCategory);
        }
        
        return Optional.empty();
    }
    
    /**
     * Checks if a category name represents an invalid AI response
     */
    public boolean isInvalidResponse(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return true;
        }
        
        String upper = categoryName.trim().toUpperCase();
        List<String> invalidResponses = Arrays.asList(
            "N/A", "NOT AVAILABLE", "NONE", "NULL", "UNKNOWN", "OTHER", 
            "GENERAL", "MISC", "MISCELLANEOUS", "UNDEFINED", "DEFAULT",
            "NOT FOUND", "ERROR", "INVALID", "EMPTY", "BLANK"
        );
        
        return invalidResponses.contains(upper);
    }
    
    /**
     * Calculates similarity between two strings using multiple methods
     */
    private double calculateSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) {
            return 0.0;
        }
        
        String lower1 = s1.toLowerCase().trim();
        String lower2 = s2.toLowerCase().trim();
        
        // Exact match
        if (lower1.equals(lower2)) {
            return 1.0;
        }
        
        // Contains match (higher weight)
        if (lower1.contains(lower2) || lower2.contains(lower1)) {
            return 0.8;
        }
        
        // Word-level matching
        double wordSimilarity = calculateWordSimilarity(lower1, lower2);
        if (wordSimilarity > 0.0) {
            return Math.max(wordSimilarity, 0.7);
        }
        
        // Levenshtein distance similarity
        return calculateLevenshteinSimilarity(lower1, lower2);
    }
    
    /**
     * Calculates similarity based on word matching
     */
    private double calculateWordSimilarity(String s1, String s2) {
        String[] words1 = s1.split("\\s+");
        String[] words2 = s2.split("\\s+");
        
        int matches = 0;
        int totalWords = Math.max(words1.length, words2.length);
        
        for (String word1 : words1) {
            for (String word2 : words2) {
                if (word1.equals(word2) || word1.contains(word2) || word2.contains(word1)) {
                    matches++;
                    break;
                }
            }
        }
        
        return totalWords > 0 ? (double) matches / totalWords : 0.0;
    }
    
    /**
     * Calculates similarity using Levenshtein distance
     */
    private double calculateLevenshteinSimilarity(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) {
            return 1.0;
        }
        
        int distance = levenshteinDistance.apply(s1, s2);
        return (maxLen - distance) / (double) maxLen;
    }
}