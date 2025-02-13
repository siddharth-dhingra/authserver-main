package com.authserver.Authserver.controller;

import com.authserver.Authserver.CustomAnnotations.RequireRoles;
import com.authserver.Authserver.model.FilterReferences.RoleEnum;
import com.authserver.Authserver.model.FilterReferences.ScanType;
import com.authserver.Authserver.model.FilterReferences.Severity;
import com.authserver.Authserver.model.FilterReferences.Status;

import java.util.Arrays;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// @RequireRoles({RoleEnum.ADMIN,RoleEnum.SUPER_ADMIN,RoleEnum.USER})
@RequestMapping("/filters")
public class FiltersController {

    @GetMapping("/statuses")
    public ResponseEntity<Status[]> getAllStatuses() {
        return ResponseEntity.ok(Status.values());
    }

    @GetMapping("/toolTypes")
    public ResponseEntity<ScanType[]> getAllToolTypes() {
        ScanType[] filteredValues = Arrays.stream(ScanType.values())
            .filter(scanType -> scanType != ScanType.ALL)
            .toArray(ScanType[]::new);

        return ResponseEntity.ok(filteredValues);
    }

    @GetMapping("/severities")
    public ResponseEntity<Severity[]> getAllSeverities() {
        return ResponseEntity.ok(Severity.values());
    }
}