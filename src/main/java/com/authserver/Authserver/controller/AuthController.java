package com.authserver.Authserver.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

        // Assuming the principal is the Google ID (sub attribute)
        String googleId = auth.getName();

        Optional<UserRole> roleOpt = userRoleRepository.findByGoogleId(googleId);
        if (!roleOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User role not found");
        }
        UserRole userRole = roleOpt.get();

        Map<String, Object> out = new HashMap<>();
        out.put("email", auth.getName());
        out.put("role", userRole.getRole());
        return out;
    }
}
