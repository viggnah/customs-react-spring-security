package com.tra.customs.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Base64;

@Component
public class JwtDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            System.out.println("=== JWT DEBUG FILTER ===");
            System.out.println("Request URL: " + request.getRequestURL());
            System.out.println("Authorization Header: " + authHeader.substring(0, Math.min(50, authHeader.length())) + "...");
            System.out.println("JWT Token (first 100 chars): " + token.substring(0, Math.min(100, token.length())) + "...");
            
            try {
                // Parse JWT parts
                String[] parts = token.split("\\.");
                if (parts.length >= 2) {
                    // Decode header
                    String header = new String(Base64.getUrlDecoder().decode(parts[0]));
                    System.out.println("JWT Header: " + header);
                    
                    // Decode payload
                    String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
                    System.out.println("JWT Payload: " + payload);
                }
            } catch (Exception e) {
                System.out.println("Error parsing JWT: " + e.getMessage());
            }
            
            System.out.println("========================");
        }
        
        filterChain.doFilter(request, response);
    }
}
