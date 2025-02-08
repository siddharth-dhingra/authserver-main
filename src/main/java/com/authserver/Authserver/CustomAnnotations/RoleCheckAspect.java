package com.authserver.Authserver.CustomAnnotations;

import com.authserver.Authserver.model.RoleEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RoleCheckAspect {

    @Around("@annotation(requireRoles)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRoles requireRoles) throws Throwable {
        // Grab the roles from the annotation
        RoleEnum[] allowed = requireRoles.value();
        // Check current user's role from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            for (GrantedAuthority ga : auth.getAuthorities()) {
                // Typically "ROLE_USER", "ROLE_ADMIN", etc.
                String authority = ga.getAuthority();
                for (RoleEnum re : allowed) {
                    if (authority.equals("ROLE_" + re.name())) {
                        // The user has an allowed role => proceed
                        return joinPoint.proceed();
                    }
                }
            }
        }
        // If we reach here, not authorized
        throw new AccessDeniedException("You do not have permission to access this endpoint.");
    }
}
