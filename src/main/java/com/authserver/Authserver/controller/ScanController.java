package com.authserver.Authserver.controller;

import com.authserver.Authserver.CustomAnnotations.RequireRoles;
import com.authserver.Authserver.dto.ScanRequestEvent;
import com.authserver.Authserver.model.ScanEvent;
import com.authserver.Authserver.model.FilterReferences.RoleEnum;
import com.authserver.Authserver.model.FilterReferences.ToolType;
import com.authserver.Authserver.producer.ScanEventProducer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequireRoles({RoleEnum.ADMIN,RoleEnum.SUPER_ADMIN})
@RequestMapping("/alert")
public class ScanController {

    private final ScanEventProducer scanEventProducer;

    public ScanController(ScanEventProducer scanEventProducer) {
        this.scanEventProducer = scanEventProducer;
    }

    @PostMapping("/scan")
    public ResponseEntity<String> initiateScan(@RequestBody Map<String, List<String>> request, @RequestParam String tenantId) {

        List<String> typesFromRequest = request.get("types");

        List<ToolType> toolTypes;
        if (typesFromRequest.contains("ALL")) {
            toolTypes = Arrays.asList(ToolType.CODESCAN, ToolType.DEPENDABOT, ToolType.SECRETSCAN);
        } else {
            toolTypes = typesFromRequest.stream()
                    .map(String::toUpperCase)
                    .map(ToolType::valueOf)
                    .collect(Collectors.toList());
        }

        for (ToolType toolType : toolTypes) {
            ScanEvent scanEvent = new ScanEvent(tenantId, toolType,null);
            ScanRequestEvent event = new ScanRequestEvent(scanEvent,null);
            scanEventProducer.sendScanEvent(event);
        }
        
        return ResponseEntity.ok("Scan event sent successfully.");
    }
}