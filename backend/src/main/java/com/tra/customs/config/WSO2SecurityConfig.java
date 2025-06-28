package com.tra.customs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@Profile("wso2")
public class WSO2SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean("wso2FilterChain")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .authorizeHttpRequests(authz -> authz
                // Public endpoints
                .requestMatchers("/api/auth/validate", "/api/auth/refresh").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                
                // Admin endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // Cargo endpoints
                .requestMatchers("/api/cargo/**").hasAnyAuthority(
                    "READ", "WRITE", "INSPECT_CARGO", "PROCESS_CLEARANCE"
                )
                
                // Vehicle endpoints
                .requestMatchers("/api/vehicles/**").hasAnyAuthority(
                    "READ", "WRITE", "INSPECT_VEHICLE"
                )
                
                // Duty endpoints
                .requestMatchers("/api/duties/**").hasAnyAuthority(
                    "READ", "WRITE", "CALCULATE_DUTY", "PROCESS_PAYMENT"
                )
                
                // Reports endpoints
                .requestMatchers("/api/reports/**").hasAnyAuthority(
                    "READ", "VIEW_REPORTS"
                )
                
                // Dashboard endpoint
                .requestMatchers("/api/dashboard/**").hasAnyAuthority("READ")
                
                // All other requests require authentication
                .anyRequest().authenticated()
            );

        // H2 Console specific configuration (only for development)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        
        // Convert JWT claims to Spring Security authorities
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Get groups from JWT (WSO2 IS groups claim)
            List<String> groups = jwt.getClaimAsStringList("groups");
            if (groups == null) {
                groups = Collections.emptyList();
            }
            
            // Map groups to TRA roles and authorities
            return groups.stream()
                .flatMap(group -> mapGroupToAuthorities(group).stream())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        });
        
        return converter;
    }

    /**
     * Maps WSO2 IS groups to TRA authorities
     */
    private Collection<String> mapGroupToAuthorities(String group) {
        Map<String, List<String>> groupToAuthorities = Map.of(
            "admin", List.of("ROLE_ADMIN", "READ", "WRITE", "DELETE", "MANAGE_USERS", "VIEW_REPORTS"),
            "customs_officer", List.of("ROLE_CUSTOMS_OFFICER", "READ", "WRITE", "PROCESS_CLEARANCE", "VIEW_REPORTS"),
            "cargo_inspector", List.of("ROLE_CARGO_INSPECTOR", "READ", "WRITE", "INSPECT_CARGO"),
            "vehicle_inspector", List.of("ROLE_VEHICLE_INSPECTOR", "READ", "WRITE", "INSPECT_VEHICLE"),
            "duty_officer", List.of("ROLE_DUTY_OFFICER", "READ", "WRITE", "CALCULATE_DUTY", "PROCESS_PAYMENT")
        );
        
        return groupToAuthorities.getOrDefault(group.toLowerCase(), Collections.emptyList());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
