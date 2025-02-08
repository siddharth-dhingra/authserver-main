package com.authserver.Authserver.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.authserver.Authserver.model.Finding;
import com.authserver.Authserver.model.ScanType;
import com.authserver.Authserver.model.Severity;
import com.authserver.Authserver.model.Status;
import com.authserver.Authserver.dto.PageDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElasticsearchService {

    private final ElasticsearchClient esClient;

    @Value("${app.elasticsearch.findings-index}")
    private String findingsIndex;

    public ElasticsearchService(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    public PageDTO<Finding> findFiltered(
            List<ScanType> toolTypes,
            List<Status> statuses,
            List<Severity> severities,
            int page,
            int size) throws IOException {

        Query boolQuery = buildBoolQuery(toolTypes, statuses, severities);
        int from = (page - 1) * size;

        SearchRequest request = SearchRequest.of(s -> s
                .index(findingsIndex)
                .query(boolQuery)
                .sort(sort -> sort
                        .field(f -> f
                                .field("updatedAt")
                                .order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)
                        )
                )
                .from(from)
                .size(size)
        );

        SearchResponse<Finding> response = esClient.search(request, Finding.class);

        // Extract total hits
        long totalHits = 0L;
        if (response.hits().total() != null) {
            totalHits = response.hits().total().value();
        }

        // Extract actual findings
        List<Finding> findings = response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();

        // Return a DTO with both findings & totalHits
        return new PageDTO<>(findings, totalHits);
    }

    private Query buildBoolQuery(
            List<ScanType> toolTypes,
            List<Status> statuses,
            List<Severity> severities
    ) {
        Query toolTypeShould = buildShouldQuery(toolTypes,"toolType.keyword");
        Query statusShould   = buildShouldQuery(statuses,"status.keyword");
        Query severityShould = buildShouldQuery(severities,"severity.keyword");

        return combineMustQueries(toolTypeShould, statusShould, severityShould);
    }

    private <T extends Enum<T>> Query buildShouldQuery(List<T> filterTypes, String fieldName) {
        if (filterTypes == null || filterTypes.isEmpty()) {
            return null;
        }
        List<Query> shouldQueries = filterTypes.stream()
                .map(toolType -> Query.of(q -> q.term(t -> t
                        .field(fieldName)
                        .value(toolType.toString())
                )))
                .collect(Collectors.toList());

        BoolQuery boolQuery = new BoolQuery.Builder()
                .should(shouldQueries)
                .build();

        return Query.of(q -> q.bool(boolQuery));
    }

    private Query combineMustQueries(Query... queries) {
        List<Query> nonNullQueries = new ArrayList<>();
        for (Query query : queries) {
            if (query != null) {
                nonNullQueries.add(query);
            }
        }

        if (nonNullQueries.isEmpty()) {
            return Query.of(q -> q.matchAll(m -> m));
        }

        BoolQuery boolQuery = new BoolQuery.Builder()
                .must(nonNullQueries)
                .build();

        return Query.of(q -> q.bool(boolQuery));
    }
}
