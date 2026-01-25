package com.example.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final KeycloakRoleMapper keycloakRoleMapper;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/error", "/images/**", "/media/**").permitAll()
                        .requestMatchers("/home").hasRole("USER")
                        .requestMatchers("/posts/**").hasRole("USER")
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user", "/user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                (req, res, e) -> res.sendRedirect("/login")
                        )
                )

                .oauth2Login(oauth -> oauth
                        .successHandler((request, response, authentication) -> {

                            var authorities = authentication.getAuthorities();

                            boolean isAdmin = authorities.stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                            boolean isUser = authorities.stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

                            if (isAdmin) {
                                response.sendRedirect("/admin");
                            } else if (isUser) {
                                response.sendRedirect("/user");
                            } else {
                                response.sendRedirect("/login");
                            }
                        })
                )

                .logout(logout -> logout
                        .logoutSuccessHandler((request, response, authentication) -> {

                            String logoutUrl =
                                    "https://103.118.29.245:8443/realms/thanh/protocol/openid-connect/logout"
                                            + "?client_id=thanh-api"
                                            + "&post_logout_redirect_uri=http://localhost:8080/login";

                            response.sendRedirect(logoutUrl);
                        })
                );

        return http.build();
    }
}