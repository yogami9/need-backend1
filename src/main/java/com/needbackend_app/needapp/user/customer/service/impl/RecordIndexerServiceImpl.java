package com.needbackend_app.needapp.user.customer.service.impl;

import com.needbackend_app.needapp.user.customer.dto.CategoryDocumentDTO;
import com.needbackend_app.needapp.user.customer.dto.RecordDocument;
import com.needbackend_app.needapp.user.customer.dto.SubCategoryDocumentDTO;
import com.needbackend_app.needapp.user.customer.service.RecordIndexerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecordIndexerServiceImpl implements RecordIndexerService {

    private final ElasticsearchTemplate elasticsearchTemplate;

    private static final String INDEX_NAME = "records";


    @Override
    public void indexCategory(CategoryDocumentDTO category) {
        RecordDocument doc = new RecordDocument(
                category.id(),
                category.name(),
                category.description(),
                category.name(),
                category.description(),
                "category",
                null,
                category.name()
        );
        IndexQuery query = IndexQuery.builder()
                .withId(doc.id().toString())
                .withObject(doc)
                .build();

        elasticsearchTemplate.index(query, IndexCoordinates.of(INDEX_NAME));
    }

    @Override
    public void  indexSubCategory(SubCategoryDocumentDTO subCategory) {
        RecordDocument doc = new RecordDocument(
                subCategory.id(),
                subCategory.name(),
                subCategory.description(),
                subCategory.name(),
                subCategory.description(),
                "subcategory",
                subCategory.categoryId(),
                null
        );

        IndexQuery query = IndexQuery.builder()
                .withId(doc.id().toString())
                .withObject(doc)
                .build();

        elasticsearchTemplate.index(query, IndexCoordinates.of(INDEX_NAME));
    }
}
