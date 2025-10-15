package com.needbackend_app.needapp.user.customer.service;

import com.needbackend_app.needapp.user.customer.dto.SubCategoryDocumentDTO;

import java.util.List;
import java.util.UUID;

public interface CustomSubCategorySearch {
    List<SubCategoryDocumentDTO> searchByTextAndCategoryId(String query, UUID categoryId);
}
