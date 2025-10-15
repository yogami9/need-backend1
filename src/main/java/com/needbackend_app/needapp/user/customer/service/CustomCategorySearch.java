package com.needbackend_app.needapp.user.customer.service;

import com.needbackend_app.needapp.user.customer.dto.CategoryDocumentDTO;

import java.util.List;

public interface CustomCategorySearch {
    List<CategoryDocumentDTO> searchByText(String query);
}
