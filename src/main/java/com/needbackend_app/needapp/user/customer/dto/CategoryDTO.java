package com.needbackend_app.needapp.user.customer.dto;

import com.needbackend_app.needapp.user.customer.model.SubCategory;

import java.util.List;
import java.util.UUID;


public record CategoryDTO (

     UUID id,
     String name,
     String description,
     List<SubCategoryDTO> subCategories
) {}
