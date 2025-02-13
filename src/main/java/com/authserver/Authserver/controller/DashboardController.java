package com.authserver.Authserver.controller;

import com.authserver.Authserver.CustomAnnotations.RequireRoles;
import com.authserver.Authserver.model.FilterReferences.RoleEnum;
import com.authserver.Authserver.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequireRoles({RoleEnum.ADMIN,RoleEnum.SUPER_ADMIN,RoleEnum.USER})
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/alerts/toolCounts")
    public ResponseEntity<Map<String, Long>> getAlertsPerTool(@RequestParam String tenantId) {
        Map<String, Long> data = dashboardService.getAlertsPerTool(tenantId);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/alerts/stateCounts")
    public ResponseEntity<Map<String, Long>> getAlertsPerState(@RequestParam String tenantId, @RequestParam(required = false) String tool) {
        Map<String, Long> data = dashboardService.getAlertsPerState(tenantId, tool);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/alerts/severityCounts")
    public ResponseEntity<Map<String, Long>> getAlertsPerSeverity(@RequestParam String tenantId, @RequestParam(required = false) String tool) {
        Map<String, Long> data = dashboardService.getAlertsPerSeverity(tenantId, tool);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/alerts/cvssHistogram")
    public ResponseEntity<List<Map<String, Object>>> getCvssHistogram(@RequestParam String tenantId, @RequestParam(required = false) String tool) {
        List<Map<String, Object>> data = dashboardService.getCvssHistogram(tenantId, tool);
        return ResponseEntity.ok(data);
    }
}