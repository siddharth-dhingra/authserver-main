package com.authserver.Authserver.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.authserver.Authserver.model.Finding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class DashboardService {

    @Value("${app.elasticsearch.findings-index}")
    private String findingsIndex;

    private final ElasticsearchClient esClient;

    public DashboardService(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    public Map<String, Long> getAlertsPerTool() {
        // group by toolType.keyword
        try {
            SearchRequest req = SearchRequest.of(s -> s
                .index(findingsIndex)
                .size(0) // we only want aggregations, no documents
                .aggregations("toolAgg", a -> a
                    .terms(t -> t.field("toolType.keyword").size(10))
                )
            );

            SearchResponse<Finding> resp = esClient.search(req, Finding.class);
            StringTermsAggregate agg = resp.aggregations()
                                         .get("toolAgg")
                                         .sterms();
            Map<String, Long> result = new HashMap<>();
            for (StringTermsBucket b : agg.buckets().array()) {
                result.put(b.key().stringValue(), b.docCount());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    public Map<String, Long> getAlertsPerState(String tool) {
        // group by status.keyword
        try {
            SearchRequest req = SearchRequest.of(s -> s
                .index(findingsIndex)
                .size(0)
                .query(q -> {
                    if (tool != null && !tool.equalsIgnoreCase("ALL")) {
                        return q.term(t -> t.field("toolType.keyword").value(tool.toUpperCase()));
                    } else {
                        return q.matchAll(m -> m);
                    }
                })
                .aggregations("stateAgg", a -> a
                    .terms(t -> t.field("status.keyword").size(10))
                )
            );

            SearchResponse<Finding> resp = esClient.search(req, Finding.class);
            StringTermsAggregate agg = resp.aggregations()
                                         .get("stateAgg")
                                         .sterms();
            Map<String, Long> result = new HashMap<>();
            for (StringTermsBucket b : agg.buckets().array()) {
                result.put(b.key().stringValue(), b.docCount());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    public Map<String, Long> getAlertsPerSeverity(String tool) {
        // group by severity.keyword
        try {
            SearchRequest req = SearchRequest.of(s -> s
                .index(findingsIndex)
                .size(0)
                .query(q -> {
                    if (tool != null && !tool.equalsIgnoreCase("ALL")) {
                        return q.term(t -> t.field("toolType.keyword").value(tool.toUpperCase()));
                    } else {
                        return q.matchAll(m -> m);
                    }
                })
                .aggregations("severityAgg", a -> a
                    .terms(t -> t.field("severity.keyword").size(10))
                )
            );

            SearchResponse<Finding> resp = esClient.search(req, Finding.class);
            StringTermsAggregate agg = resp.aggregations()
                                         .get("severityAgg")
                                         .sterms();
            Map<String, Long> result = new HashMap<>();
            for (StringTermsBucket b : agg.buckets().array()) {
                result.put(b.key().stringValue(), b.docCount());
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    public List<Map<String, Object>> getCvssHistogram(String tool) {
        try {
            SearchRequest req = SearchRequest.of(s -> s
                .index(findingsIndex)
                .size(0)
                .query(q -> {
                    if (tool != null && !tool.equalsIgnoreCase("ALL")) {
                        return q.term(t -> t.field("toolType.keyword").value(tool.toUpperCase()));
                    } else {
                        return q.matchAll(m -> m);
                    }
                })
                .aggregations("cvssHist", a -> a
                    .histogram(h -> h
                        .field("cvss")
                        .interval(1.0) 
                        .minDocCount(0)
                    )
                )
            );

            SearchResponse<Finding> resp = esClient.search(req, Finding.class);
            HistogramAggregate agg = resp.aggregations().get("cvssHist").histogram();

            List<Map<String, Object>> result = new ArrayList<>();
            for (HistogramBucket b : agg.buckets().array()) {
                Map<String, Object> bucket = new HashMap<>();
                bucket.put("key", b.key());        
                bucket.put("count", b.docCount()); 
                result.add(bucket);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}