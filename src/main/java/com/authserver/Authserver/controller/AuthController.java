package com.authserver.Authserver.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.authserver.Authserver.model.UserRole;
import com.authserver.Authserver.repository.UserRoleRepository;
import java.util.Optional;


@RestController
public class AuthController {

    private final UserRoleRepository userRoleRepository;

    public AuthController(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @GetMapping("/whoami")
    public Map<String, Object> whoAmI() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (!(auth.getPrincipal() instanceof DefaultOAuth2User)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Principal not an OAuth2 user");
        }

        DefaultOAuth2User principal = (DefaultOAuth2User) auth.getPrincipal();
        String googleId = (String) principal.getAttributes().get("sub");

        String actualEmail = (String) principal.getAttributes().get("name");
        if (actualEmail == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No email in user attributes");
        }

        Optional<UserRole> roleOpt = userRoleRepository.findByGoogleId(googleId);
        if (!roleOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User role not found for Google ID=" + googleId);
        }
        UserRole userRole = roleOpt.get();

        Map<String, Object> out = new HashMap<>();
        out.put("email", actualEmail);
        out.put("role", userRole.getRole());
        return out;
    }
}
