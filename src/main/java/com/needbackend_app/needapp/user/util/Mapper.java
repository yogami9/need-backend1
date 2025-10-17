package com.needbackend_app.needapp.user.util;

import com.needbackend_app.needapp.user.customer.dto.CategoryDTO;
import com.needbackend_app.needapp.user.customer.dto.SubCategoryDTO;
import com.needbackend_app.needapp.user.customer.model.Category;
import com.needbackend_app.needapp.user.customer.model.SubCategory;

import java.util.stream.Collectors;

public class Mapper {

    public static CategoryDTO toCategoryDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getSubCategories()
                        .stream()
                        .map(Mapper::toSubCategoryDTO)
                        .collect(Collectors.toList())
        );
    }

    public static SubCategoryDTO toSubCategoryDTO(SubCategory subCategory) {
        return new SubCategoryDTO(
                subCategory.getId(),
                subCategory.getName(),
                subCategory.getDescription()
        );
    }
}
