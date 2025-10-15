package com.needbackend_app.needapp.user.customer.service.impl;

import com.needbackend_app.needapp.user.customer.dto.CategoryDocumentDTO;
import com.needbackend_app.needapp.user.customer.dto.RecordDocument;
import com.needbackend_app.needapp.user.customer.service.CustomCategorySearch;
import com.needbackend_app.needapp.user.util.QueryPreprocessor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomCategorySearchImpl implements CustomCategorySearch {

    private final ElasticsearchOperations elasticsearchOperations;
    private final QueryPreprocessor queryPreprocessor;

    @Override
    public List<CategoryDocumentDTO> searchByText(String query) {
        String cleanedQuery = queryPreprocessor.preprocessor(query);

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .bool(b -> b
                                .should(s1 -> s1
                                        .multiMatch(mm -> mm
                                                .query(cleanedQuery)
                                                .fields("autocomplete_synonym^10",  "description_autocomplete^3")
                                                .fuzziness("AUTO")
                                        )
                                )
                                .should(s2 -> s2
                                        .multiMatch(mm -> mm
                                                .query(cleanedQuery)
                                                .fields("name^4", "description^2")
                                                .fuzziness("AUTO")
                                        )
                                )
                                .filter(f -> f
                                        .term(t -> t
                                                .field("type")
                                                .value("category")
                                        )
                                )
                                .minimumShouldMatch("1")
                        )
                )
                .withPageable(PageRequest.of(0, 5))
                .build();

        SearchHits<RecordDocument> hits = elasticsearchOperations.search(nativeQuery, RecordDocument.class);

        return hits.stream()
                .map(hit -> new CategoryDocumentDTO(
                        hit.getContent().id(),
                        hit.getContent().name(),
                        hit.getContent().description()
                ))
                .toList();
    }

}