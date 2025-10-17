package com.needbackend_app.needapp.user.customer.service.impl;

import com.needbackend_app.needapp.user.customer.dto.RecordDocument;
import com.needbackend_app.needapp.user.customer.dto.SubCategoryDocumentDTO;
import com.needbackend_app.needapp.user.customer.service.CustomSubCategorySearch;
import com.needbackend_app.needapp.user.util.QueryPreprocessor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomSubCategorySearchImpl implements CustomSubCategorySearch {

    private final ElasticsearchOperations elasticsearchOperations;
    private final QueryPreprocessor queryPreprocessor;

    @Override
    public List<SubCategoryDocumentDTO> searchByTextAndCategoryId(String query, UUID categoryId) {
        String cleanedQuery = queryPreprocessor.preprocessor(query);

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .bool(b -> b
                                .should(s -> s
                                        .multiMatch(mm -> mm
                                                .query(cleanedQuery)
                                                .fields("autocomplete_synonym^10",  "description_autocomplete^3")
                                                .fuzziness("AUTO")
                                        )
                                )
                                .should(s -> s
                                        .multiMatch(mm -> mm
                                                .query(cleanedQuery)
                                                .fields("name^4", "description^2")
                                                .fuzziness("AUTO")
                                        )
                                )
                                .filter(f -> f
                                        .term(t -> t
                                                .field("categoryId")
                                                .value(categoryId.toString()))
                                )
                                .minimumShouldMatch("1")
                        )


                        )
                .withPageable(PageRequest.of(0, 5))
                .build();
        SearchHits<RecordDocument> hits = elasticsearchOperations.search(nativeQuery, RecordDocument.class);
        return hits.stream()
                .map(hit -> new SubCategoryDocumentDTO(
                        hit.getContent().id(),
                        hit.getContent().name(),
                        hit.getContent().description(),
                        hit.getContent().categoryId()
                ))
                .toList();
    }


}
