package com.authserver.Authserver.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.UpdateRequest;

import com.authserver.Authserver.model.Finding;
import com.authserver.Authserver.model.Tenant;
import com.authserver.Authserver.model.FilterReferences.ScanType;
import com.authserver.Authserver.model.FilterReferences.Severity;
import com.authserver.Authserver.model.FilterReferences.Status;
import com.authserver.Authserver.repository.TenantRepository;
import com.authserver.Authserver.dto.PageDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElasticsearchService {

    private final ElasticsearchClient esClient;
    private final TenantRepository tenantRepository;

    public ElasticsearchService(ElasticsearchClient esClient, TenantRepository tenantRepository) {
        this.esClient = esClient;
        this.tenantRepository = tenantRepository;
    }

    private String getFindingsIndex(String tenantId) {
        Tenant tenant = tenantRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new RuntimeException("Invalid tenantId: " + tenantId));
        return tenant.getEsIndex();
    }

    public Finding getSingleFinding(String tenantId, String findingId) {
        String indexName = getFindingsIndex(tenantId);
        try {
            GetResponse<Finding> response = esClient.get(
                GetRequest.of(g -> g.index(indexName).id(findingId)),
                Finding.class
            );
            if(response.found()) {
                return response.source();
            } else {
                throw new RuntimeException("Finding not found for id: " + findingId);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch finding with id: " + findingId, e);
        }
    }

    public PageDTO<Finding> findFiltered(
            String tenantId,
            List<ScanType> toolTypes,
            List<Status> statuses,
            List<Severity> severities,
            int page,
            int size) throws IOException {

        String findingsIndex = getFindingsIndex(tenantId);

        boolean indexExists = esClient.indices().exists(e -> e.index(findingsIndex)).value();
        if (!indexExists) {
            return new PageDTO<>(new ArrayList<>(), 0);
        }

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

        long totalHits = 0L;
        if (response.hits().total() != null) {
            totalHits = response.hits().total().value();
        }

        List<Finding> findings = response.hits().hits().stream()
                .map(hit -> hit.source())
                .toList();

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

    public void updateFindingWithTicketId(String tenantId, String findingId, String ticketId) {
        String indexName = getFindingsIndex(tenantId);

        Finding findingUpdate = new Finding();
        findingUpdate.setTicketId(ticketId);
        findingUpdate.setUpdatedAt(LocalDateTime.now().toString());

        System.out.println("Updating finding with ticket ID: " + ticketId);
        System.out.println("Finding ID: " + findingId);
        System.out.println("Index name: " + indexName);
        
        UpdateRequest<Finding, Finding> updateRequest = UpdateRequest.of(u -> u
            .index(indexName)
            .id(findingId)
            .doc(findingUpdate)
        );

        try {
            esClient.update(updateRequest, Finding.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to update finding with ticket ID", e);
        }
    }
}
