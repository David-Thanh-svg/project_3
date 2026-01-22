package com.example.config;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class KeycloakRoleMapper implements GrantedAuthoritiesMapper {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<? extends GrantedAuthority> mapAuthorities(
            Collection<? extends GrantedAuthority> authorities
    ) {

        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

        for (GrantedAuthority authority : authorities) {

            Map<String, Object> claims = null;

            // OIDC login (Thymeleaf)
            if (authority instanceof OidcUserAuthority oidcAuth) {
                claims = oidcAuth.getUserInfo().getClaims();
            }

            // OAuth2 login thường
            else if (authority instanceof OAuth2UserAuthority oauth2Auth) {
                claims = oauth2Auth.getAttributes();
            }

            if (claims == null) continue;

            Map<String, Object> realmAccess =
                    (Map<String, Object>) claims.get("realm_access");

            if (realmAccess == null || realmAccess.get("roles") == null) continue;

            Collection<String> roles =
                    (Collection<String>) realmAccess.get("roles");

            mappedAuthorities.addAll(
                    roles.stream()
                            .filter(r -> !r.startsWith("default-") && !r.startsWith("uma_"))
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                            .collect(Collectors.toSet())
            );
        }

        return mappedAuthorities;
    }
}
