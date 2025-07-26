package com.customs.management.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WSO2UserService {

    /**
     * Extracts user information from WSO2 IS JWT token
     */
    public Map<String, Object> extractUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Jwt jwt = (Jwt) authentication.getPrincipal();
        
        Map<String, Object> userInfo = new HashMap<>();
        
        // Basic user information
        userInfo.put("sub", jwt.getClaimAsString("sub"));
        userInfo.put("username", getUsernameFromJwt(jwt));
        userInfo.put("email", jwt.getClaimAsString("email"));
        userInfo.put("firstName", jwt.getClaimAsString("given_name"));
        userInfo.put("lastName", jwt.getClaimAsString("family_name"));
        userInfo.put("fullName", getFullName(jwt));
        
        // WSO2 IS specific claims
        userInfo.put("groups", jwt.getClaimAsStringList("groups"));
        userInfo.put("tenant", jwt.getClaimAsString("tenant"));
        userInfo.put("organizationId", jwt.getClaimAsString("organization_id"));
        
        // Spring Security authorities
        List<String> authorities = authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .toList();
            
        userInfo.put("authorities", authorities);
        
        // Extract roles (authorities starting with ROLE_)
        List<String> roles = authorities.stream()
            .filter(auth -> auth.startsWith("ROLE_"))
            .map(role -> role.substring(5)) // Remove "ROLE_" prefix
            .toList();
            
        userInfo.put("roles", roles);
        
        // Extract permissions (authorities not starting with ROLE_)
        List<String> permissions = authorities.stream()
            .filter(auth -> !auth.startsWith("ROLE_"))
            .toList();
            
        userInfo.put("permissions", permissions);
        
        // Token metadata
        userInfo.put("issuedAt", jwt.getIssuedAt());
        userInfo.put("expiresAt", jwt.getExpiresAt());
        userInfo.put("issuer", jwt.getIssuer());
        
        return userInfo;
    }
    
    /**
     * Gets username from JWT, trying multiple claim names
     */
    private String getUsernameFromJwt(Jwt jwt) {
        // Try different username claims that WSO2 IS might use
        String username = jwt.getClaimAsString("preferred_username");
        if (username != null) return username;
        
        username = jwt.getClaimAsString("username");
        if (username != null) return username;
        
        username = jwt.getClaimAsString("sub");
        if (username != null) return username;
        
        // Fallback to email
        return jwt.getClaimAsString("email");
    }
    
    /**
     * Constructs full name from given_name and family_name
     */
    private String getFullName(Jwt jwt) {
        String givenName = jwt.getClaimAsString("given_name");
        String familyName = jwt.getClaimAsString("family_name");
        
        if (givenName != null && familyName != null) {
            return givenName + " " + familyName;
        } else if (givenName != null) {
            return givenName;
        } else if (familyName != null) {
            return familyName;
        }
        
        return getUsernameFromJwt(jwt);
    }
    
    /**
     * Checks if user has a specific role
     */
    public boolean hasRole(Authentication authentication, String role) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities()
            .stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_" + role));
    }
    
    /**
     * Checks if user has a specific authority/permission
     */
    public boolean hasAuthority(Authentication authentication, String authority) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        return authentication.getAuthorities()
            .stream()
            .anyMatch(auth -> auth.getAuthority().equals(authority));
    }
    
    /**
     * Gets the user's primary role (first role found)
     */
    public String getPrimaryRole(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        return authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .filter(auth -> auth.startsWith("ROLE_"))
            .map(role -> role.substring(5))
            .findFirst()
            .orElse(null);
    }
}
