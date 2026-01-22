package com.example.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleBasedLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        for (GrantedAuthority authority : authentication.getAuthorities()) {

            String role = authority.getAuthority();

            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin");
                return;
            }

            if (role.equals("ROLE_USER")) {
                response.sendRedirect("/user");
                return;
            }
        }

        // fallback
        response.sendRedirect("/");
    }
}