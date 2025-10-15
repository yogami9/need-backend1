package com.needbackend_app.needapp.user.customer.service;

import com.needbackend_app.needapp.user.customer.dto.CategoryDTO;
import com.needbackend_app.needapp.user.customer.dto.CategoryDocumentDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    List<CategoryDTO> getAllCategories();

    CategoryDTO getCategoryById(UUID id);



}
