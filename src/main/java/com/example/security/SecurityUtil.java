package com.example.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityUtil {

    private static Jwt getJwt(Authentication authentication) {
        if (authentication == null) {
            throw new AccessDeniedException("Unauthenticated");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof Jwt jwt)) {
            throw new AccessDeniedException("Invalid authentication principal");
        }

        return jwt;
    }

    public static String getKeycloakId(Authentication authentication) {
        return getJwt(authentication).getSubject();
    }

    public static String getUsername(Authentication authentication) {
        return getJwt(authentication).getClaimAsString("preferred_username");
    }

    public static String getEmail(Authentication authentication) {
        return getJwt(authentication).getClaimAsString("email");
    }
}
