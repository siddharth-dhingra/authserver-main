package com.authserver.Authserver.controller;

import com.authserver.Authserver.model.Finding;
import com.authserver.Authserver.model.FilterReferences.RoleEnum;
import com.authserver.Authserver.model.FilterReferences.ScanType;
import com.authserver.Authserver.model.FilterReferences.Severity;
import com.authserver.Authserver.model.FilterReferences.Status;
import com.authserver.Authserver.service.ElasticsearchService;
import com.authserver.Authserver.CustomAnnotations.RequireRoles;
import com.authserver.Authserver.dto.PageDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequireRoles({RoleEnum.ADMIN,RoleEnum.SUPER_ADMIN,RoleEnum.USER})
@RequestMapping("/alerts/finding")
public class FindingsController {

    private final ElasticsearchService elasticsearchService;

    public FindingsController(ElasticsearchService elasticsearchService) {
        this.elasticsearchService = elasticsearchService;
    }

    @GetMapping("/search")
    public ResponseEntity<PageDTO<Finding>> searchFindings(
            @RequestParam String tenantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) List<Status> statuses,
            @RequestParam(required = false) List<Severity> severities,
            @RequestParam(required = false) List<ScanType> toolTypes) {

        try {
            PageDTO<Finding> results = elasticsearchService.findFiltered(tenantId, toolTypes, statuses, severities, page, size);
            return ResponseEntity.ok(results);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
