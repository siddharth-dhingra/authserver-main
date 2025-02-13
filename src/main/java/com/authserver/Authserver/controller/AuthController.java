package com.authserver.Authserver.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.authserver.Authserver.model.OAuthUser;
import com.authserver.Authserver.model.UserRole;
import com.authserver.Authserver.repository.OAuthUserRepository;
import com.authserver.Authserver.repository.UserRoleRepository;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
public class AuthController {

    private final UserRoleRepository userRoleRepository;
    private final OAuthUserRepository oAuthUserRepository;

    public AuthController(UserRoleRepository userRoleRepository, OAuthUserRepository oAuthUserRepository) {
        this.userRoleRepository = userRoleRepository;
        this.oAuthUserRepository = oAuthUserRepository;
    }

    @GetMapping("/whoami")
    public Map<String, Object> whoAmI(@RequestParam(required = false) String tenantId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (!(auth.getPrincipal() instanceof DefaultOAuth2User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Principal not an OAuth2 user");
        }

        DefaultOAuth2User principal = (DefaultOAuth2User) auth.getPrincipal();
        String googleId = (String) principal.getAttributes().get("sub");

        String actualName = (String) principal.getAttributes().get("name");
        if (actualName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No name in user attributes");
        }

        OAuthUser user = oAuthUserRepository.findById(googleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (tenantId == null) {
            tenantId = user.getDefaultTenantId();
        }
        System.out.println(tenantId);
        Optional<UserRole> roleOpt = userRoleRepository.findByGoogleIdAndTenant_TenantId(googleId, tenantId);
        
        if (!roleOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User role not found for Google ID=" + googleId);
        }
        UserRole userRole = roleOpt.get();

        List<UserRole> roles = userRoleRepository.findByGoogleId(googleId);
        List<String> tenantIds = roles.stream()
                .map(role -> role.getTenant().getTenantId())
                .collect(Collectors.toList());
        List<String> tenantNames = roles.stream()
                .map(role -> role.getTenant().getTenantName())
                .collect(Collectors.toList());

        System.out.println(userRole.getRole());

        Map<String, Object> out = new HashMap<>();
        out.put("name", actualName);
        out.put("email", user.getEmail());
        out.put("defaultTenantId", user.getDefaultTenantId());
        out.put("selectedTenantId", tenantId);
        out.put("role", userRole.getRole());
        out.put("associatedTenants", tenantNames);
        out.put("associatedTenantIds", tenantIds);
        out.put("role", userRole.getRole());
        out.put("pictureUrl", user.getPictureUrl());
        return out;
    }
}
