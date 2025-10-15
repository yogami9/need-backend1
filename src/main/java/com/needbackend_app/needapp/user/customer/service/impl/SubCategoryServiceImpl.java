package com.needbackend_app.needapp.user.customer.service.impl;

import com.needbackend_app.needapp.user.customer.dto.SubCategoryDTO;
import com.needbackend_app.needapp.user.customer.repository.SubCategoryRepository;
import com.needbackend_app.needapp.user.customer.service.SubCategoryService;
import com.needbackend_app.needapp.user.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;

    @Override
    public List<SubCategoryDTO> getSubCategoriesByCategoryId(UUID categoryId) {
        return subCategoryRepository.findByCategoryId(categoryId)
                .stream()
                .map(Mapper::toSubCategoryDTO)
                .collect(Collectors.toList());
    }




}
