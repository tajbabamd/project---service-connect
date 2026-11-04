package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // Public registration and login endpoints
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/customers/register",
                    "/api/workers/register",
                    "/api/auth/login"
                ).permitAll()

                // Customer-only endpoints
                .requestMatchers("/api/customers/**")
                .hasRole("CUSTOMER")

                // Worker-only endpoints
                .requestMatchers("/api/workers/**")
                .hasRole("WORKER")

                // Administrator-only endpoints
                .requestMatchers("/api/admin/**")
                .hasRole("ADMIN")

                // Other endpoints require login
                .anyRequest().authenticated()
            )

            .exceptionHandling(exceptions -> exceptions

                // Request has no valid authentication
                .authenticationEntryPoint(
                    (request, response, authException) -> {
                        response.setStatus(
                            HttpStatus.UNAUTHORIZED.value()
                        );
                        response.setContentType("text/plain");
                        response.getWriter().write("Unauthorized");
                    }
                )

                // Authenticated user has insufficient permissions
                .accessDeniedHandler(
                    (request, response, accessDeniedException) -> {
                        response.setStatus(
                            HttpStatus.FORBIDDEN.value()
                        );
                        response.setContentType("text/plain");
                        response.getWriter().write("Access denied");
                    }
                )
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}