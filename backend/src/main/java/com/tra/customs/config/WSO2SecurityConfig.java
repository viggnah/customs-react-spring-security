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
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final JwtDebugFilter jwtDebugFilter;

    public WSO2SecurityConfig(JwtDebugFilter jwtDebugFilter) {
        this.jwtDebugFilter = jwtDebugFilter;
    }

    @Bean("wso2FilterChain")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtDebugFilter, org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter.class)
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
        try {
            System.out.println("Creating JWT decoder with JWK Set URI: " + jwkSetUri);
            
            // Create a RestTemplate that trusts all SSL certificates for development
            org.springframework.web.client.RestTemplate restTemplate = createTrustAllRestTemplate();
            
            // Create a custom JWT decoder that accepts at+jwt tokens
            return new CustomAtJwtDecoder(jwkSetUri, restTemplate);
            
        } catch (Exception e) {
            System.out.println("Failed to create JWT decoder: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Could not configure JWT decoder", e);
        }
    }
    
    private org.springframework.web.client.RestTemplate createTrustAllRestTemplate() {
        try {
            // Create a trust manager that does not validate certificate chains
            javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[] {
                new javax.net.ssl.X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        // Do nothing - trust all
                    }
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                        // Do nothing - trust all
                    }
                }
            };

            // Install the all-trusting trust manager
            javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an SSL socket factory
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Create a hostname verifier that does not validate hostnames
            javax.net.ssl.HostnameVerifier allHostsValid = new javax.net.ssl.HostnameVerifier() {
                public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            System.out.println("Successfully configured SSL trust-all for JWK retrieval");
            return new org.springframework.web.client.RestTemplate();
            
        } catch (Exception e) {
            System.out.println("Warning: Could not configure SSL trust-all, using default RestTemplate: " + e.getMessage());
            return new org.springframework.web.client.RestTemplate();
        }
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        
        // Convert JWT claims to Spring Security authorities
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            System.out.println("=== JWT Claims to Authorities Mapping Debug ===");
            System.out.println("All JWT claims: " + jwt.getClaims());
            System.out.println("Issuer: " + jwt.getIssuer());
            System.out.println("Subject: " + jwt.getSubject());
            System.out.println("Audience: " + jwt.getAudience());
            
            Collection<String> authorities = new java.util.ArrayList<>();
            
            // Try different possible group/role claims
            List<String> groups = getClaimAsList(jwt, "groups");
            List<String> roles = getClaimAsList(jwt, "roles");
            List<String> scopes = getClaimAsList(jwt, "scope");
            
            System.out.println("JWT Groups: " + groups);
            System.out.println("JWT Roles: " + roles);
            System.out.println("JWT Scopes: " + scopes);
            
            // Map groups to authorities
            groups.forEach(group -> authorities.addAll(mapGroupToAuthorities(group)));
            
            // Map roles to authorities
            roles.forEach(role -> authorities.addAll(mapRoleToAuthorities(role)));
            
            // If no groups or roles found, assign default authorities based on token type
            if (authorities.isEmpty()) {
                System.out.println("No groups/roles found in JWT");
                
                // Check if this is a client credentials token (no user context)
                String authType = jwt.getClaimAsString("aut");
                String subject = jwt.getSubject();
                
                System.out.println("Auth type: " + authType);
                System.out.println("Subject: " + subject);
                
                // For client credentials (APPLICATION auth type), assign all permissions
                if ("APPLICATION".equals(authType)) {
                    System.out.println("Client credentials token detected, assigning all authorities");
                    authorities.add("READ");
                    authorities.add("WRITE");
                    authorities.add("READ_CARGO");
                    authorities.add("CREATE_CARGO");
                    authorities.add("UPDATE_CARGO");
                    authorities.add("DELETE_CARGO");
                    authorities.add("PROCESS_CLEARANCE");
                    authorities.add("INSPECT_CARGO");
                    authorities.add("READ_VEHICLE");
                    authorities.add("CREATE_VEHICLE");
                    authorities.add("UPDATE_VEHICLE");
                    authorities.add("DELETE_VEHICLE");
                    authorities.add("INSPECT_VEHICLE");
                    authorities.add("CALCULATE_DUTY");
                    authorities.add("PROCESS_PAYMENT");
                    authorities.add("VIEW_REPORTS");
                    authorities.add("ROLE_USER");
                    authorities.add("ROLE_ADMIN");
                } else {
                    // For user tokens without groups/roles, assign minimal permissions
                    System.out.println("User token without groups/roles, assigning default READ authority");
                    authorities.add("READ");
                    authorities.add("ROLE_USER");
                }
            }
            
            System.out.println("Final mapped authorities: " + authorities);
            System.out.println("===============================================");
            
            return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        });
        
        return converter;
    }
    
    /**
     * Helper method to get claim value as a list of strings
     */
    private List<String> getClaimAsList(org.springframework.security.oauth2.jwt.Jwt jwt, String claimName) {
        Object claim = jwt.getClaim(claimName);
        if (claim == null) {
            return Collections.emptyList();
        }
        
        if (claim instanceof List) {
            return ((List<?>) claim).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        } else if (claim instanceof String) {
            // Handle space-separated or comma-separated values
            String claimStr = (String) claim;
            return Arrays.asList(claimStr.split("[\\s,]+"));
        }
        
        return Collections.singletonList(claim.toString());
    }

    /**
     * Maps WSO2 IS groups to TRA authorities
     */
    private Collection<String> mapGroupToAuthorities(String group) {
        Map<String, List<String>> groupToAuthorities = Map.of(
            "admin", List.of("ROLE_ADMIN", "READ", "WRITE", "DELETE", "MANAGE_USERS", "VIEW_REPORTS", "READ_CARGO"),
            "customs_officer", List.of("ROLE_CUSTOMS_OFFICER", "READ", "WRITE", "PROCESS_CLEARANCE", "VIEW_REPORTS"),
            "cargo_inspector", List.of("ROLE_CARGO_INSPECTOR", "READ_CARGO", "CREATE_CARGO", "INSPECT_CARGO"),
            "vehicle_inspector", List.of("ROLE_VEHICLE_INSPECTOR", "READ_VEHICLE", "CREATE_VEHICLE", "INSPECT_VEHICLE"),
            "duty_officer", List.of("ROLE_DUTY_OFFICER", "READ_DUTY", "CREATE_DUTY", "CALCULATE_DUTY", "PROCESS_PAYMENT")
        );
        
        return groupToAuthorities.getOrDefault(group.toLowerCase(), Collections.emptyList());
    }
    
    /**
     * Maps WSO2 IS roles to TRA authorities
     */
    private Collection<String> mapRoleToAuthorities(String role) {
        Map<String, List<String>> roleToAuthorities = Map.of(
            "admin", List.of("ROLE_ADMIN", "READ", "WRITE", "DELETE", "MANAGE_USERS", "VIEW_REPORTS", "READ_CARGO"),
            "customs_officer", List.of("ROLE_CUSTOMS_OFFICER", "READ", "WRITE", "PROCESS_CLEARANCE", "VIEW_REPORTS"),
            "cargo_inspector", List.of("ROLE_CARGO_INSPECTOR", "READ", "WRITE", "INSPECT_CARGO"),
            "vehicle_inspector", List.of("ROLE_VEHICLE_INSPECTOR", "READ", "WRITE", "INSPECT_VEHICLE"),
            "duty_officer", List.of("ROLE_DUTY_OFFICER", "READ", "WRITE", "CALCULATE_DUTY", "PROCESS_PAYMENT"),
            "user", List.of("ROLE_USER", "READ")
        );
        
        return roleToAuthorities.getOrDefault(role.toLowerCase(), Collections.emptyList());
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
