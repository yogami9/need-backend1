package com.needbackend_app.needapp.user.customer.dto;

import java.util.List;

public record SuggestionResponseDTO(
        List<CategoryDocumentDTO> category,
        List<SubCategoryDocumentDTO> subcategories
){
}
