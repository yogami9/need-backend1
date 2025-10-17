package com.needbackend_app.needapp.user.customer.service;

import com.needbackend_app.needapp.user.customer.dto.CategoryDocumentDTO;
import com.needbackend_app.needapp.user.customer.dto.SubCategoryDocumentDTO;
import com.needbackend_app.needapp.user.customer.dto.SuggestionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface SuggestService {
    List<CategoryDocumentDTO> suggestCategories(String query);
    List<SubCategoryDocumentDTO> suggestSubCategories(String query, UUID categoryId);
    SuggestionResponseDTO suggest(String query, UUID categoryId);
}
