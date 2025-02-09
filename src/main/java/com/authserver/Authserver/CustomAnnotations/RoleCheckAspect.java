package com.authserver.Authserver.CustomAnnotations;

import com.authserver.Authserver.model.RoleEnum;
import com.authserver.Authserver.model.UserRole;
import com.authserver.Authserver.repository.UserRoleRepository;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Aspect
@Component
public class RoleCheckAspect {

    private final UserRoleRepository userRoleRepository;

    public RoleCheckAspect(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Around("@annotation(requireRoles) || @within(requireRoles)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRoles requireRoles) throws Throwable {
        RoleEnum[] allowed = requireRoles.value();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            String googleId = extractGoogleId(auth);
            if (googleId == null) {
                throw new AccessDeniedException("Unable to determine user's Google ID from principal.");
            }

            Optional<UserRole> roleOpt = userRoleRepository.findByGoogleId(googleId);
            if (!roleOpt.isPresent()) {
                throw new AccessDeniedException("User not found in user_roles table.");
            }

            UserRole userRole = roleOpt.get();
            RoleEnum userDbRole = userRole.getRole();

            for (RoleEnum re : allowed) {
                if (userDbRole == re) {
                    return joinPoint.proceed();
                }
            }
        }
        throw new AccessDeniedException("You do not have permission to access this endpoint.");
    }

    private String extractGoogleId(Authentication auth) {
        if (auth.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User principal = (DefaultOAuth2User) auth.getPrincipal();
            Object sub = principal.getAttributes().get("sub");
            return (sub != null) ? sub.toString() : null;
        }
        return auth.getName();
    }
}
