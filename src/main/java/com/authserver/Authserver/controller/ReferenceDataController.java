package com.authserver.Authserver.controller;

import com.authserver.Authserver.model.FilterReferences.CodeScanDismissedReason;
import com.authserver.Authserver.model.FilterReferences.CodeScanState;
import com.authserver.Authserver.model.FilterReferences.DependabotDismissedReason;
import com.authserver.Authserver.model.FilterReferences.DependabotState;
import com.authserver.Authserver.model.FilterReferences.SecretScanResolution;
import com.authserver.Authserver.model.FilterReferences.SecretScanState;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
// @RequireRoles({RoleEnum.ADMIN,RoleEnum.SUPER_ADMIN,RoleEnum.USER})
@RequestMapping("/reference")
public class ReferenceDataController {

    @GetMapping("/codescan/states")
    public ResponseEntity<CodeScanState[]> getCodeScanStates() {
        return ResponseEntity.ok(CodeScanState.values());
    }

    @GetMapping("/codescan/dismissedReasons")
    public ResponseEntity<CodeScanDismissedReason[]> getCodeScanDismissedReasons() {
        return ResponseEntity.ok(CodeScanDismissedReason.values());
    }

    @GetMapping("/dependabot/states")
    public ResponseEntity<DependabotState[]> getDependabotStates() {
        return ResponseEntity.ok(DependabotState.values());
    }

    @GetMapping("/dependabot/dismissedReasons")
    public ResponseEntity<DependabotDismissedReason[]> getDependabotDismissedReasons() {
        return ResponseEntity.ok(DependabotDismissedReason.values());
    }

    @GetMapping("/secretscan/states")
    public ResponseEntity<SecretScanState[]> getSecretScanStates() {
        return ResponseEntity.ok(SecretScanState.values());
    }

    @GetMapping("/secretscan/resolutions")
    public ResponseEntity<SecretScanResolution[]> getSecretScanResolutions() {
        return ResponseEntity.ok(SecretScanResolution.values());
    }
}
