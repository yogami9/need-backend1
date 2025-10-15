package com.needbackend_app.needapp.user.customer.service;

import com.needbackend_app.needapp.user.customer.dto.CategoryDocumentDTO;
import com.needbackend_app.needapp.user.customer.dto.SubCategoryDocumentDTO;
import com.needbackend_app.needapp.user.customer.model.Category;
import com.needbackend_app.needapp.user.customer.model.SubCategory;

public interface RecordIndexerService {
    void indexCategory(CategoryDocumentDTO category);
    void indexSubCategory(SubCategoryDocumentDTO subCategory);
}
