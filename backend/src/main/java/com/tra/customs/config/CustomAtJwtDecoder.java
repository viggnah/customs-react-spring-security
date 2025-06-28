package com.tra.customs.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;

/**
 * Custom JWT Decoder that properly handles RFC 9068 Access Tokens with "typ": "at+jwt"
 * This decoder bypasses Spring Security's default type validation that rejects access tokens
 */
public class CustomAtJwtDecoder implements JwtDecoder {
    
    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;
    private final OAuth2TokenValidator<Jwt> jwtValidator;
    
    public CustomAtJwtDecoder(String jwkSetUri, RestTemplate restTemplate) {
        try {
            System.out.println("Initializing CustomAtJwtDecoder with JWK Set URI: " + jwkSetUri);
            
            // Create JWK source - we'll handle SSL trust at the global level
            JWKSource<SecurityContext> jwkSource = new RemoteJWKSet<>(new URL(jwkSetUri));
            
            // Create JWT processor
            this.jwtProcessor = new DefaultJWTProcessor<>();
            
            // Configure key selector for RS256 algorithm
            JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(
                JWSAlgorithm.RS256, jwkSource);
            this.jwtProcessor.setJWSKeySelector(keySelector);
            
            // Configure type verifier to accept both "JWT" and "at+jwt" tokens
            com.nimbusds.jose.proc.JOSEObjectTypeVerifier<SecurityContext> typeVerifier = 
                new com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier<>(
                    new com.nimbusds.jose.JOSEObjectType("JWT"),
                    new com.nimbusds.jose.JOSEObjectType("at+jwt")
                );
            this.jwtProcessor.setJWSTypeVerifier(typeVerifier);
            
            // Create a permissive validator for development
            this.jwtValidator = new OAuth2TokenValidator<Jwt>() {
                @Override
                public OAuth2TokenValidatorResult validate(Jwt jwt) {
                    System.out.println("=== Custom JWT Validator ===");
                    System.out.println("JWT Headers: " + jwt.getHeaders());
                    System.out.println("JWT Claims: " + jwt.getClaims());
                    System.out.println("JWT Issuer: " + jwt.getIssuer());
                    System.out.println("JWT Subject: " + jwt.getSubject());
                    System.out.println("JWT Expires At: " + jwt.getExpiresAt());
                    System.out.println("===============================");
                    
                    // For development, always return success
                    // In production, add proper validation here
                    return OAuth2TokenValidatorResult.success();
                }
            };
            
            System.out.println("CustomAtJwtDecoder initialized successfully");
            
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid JWK Set URI: " + jwkSetUri, e);
        }
    }
    
    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            System.out.println("=== CustomAtJwtDecoder.decode() ===");
            System.out.println("Decoding JWT token: " + token.substring(0, Math.min(50, token.length())) + "...");
            
            // Parse the JWT
            JWT jwt = JWTParser.parse(token);
            
            // Process and verify the JWT
            JWTClaimsSet claimsSet = jwtProcessor.process(jwt, null);
            
            System.out.println("JWT successfully processed by Nimbus");
            System.out.println("JWT Header: " + jwt.getHeader().toJSONObject());
            System.out.println("JWT Claims: " + claimsSet.toJSONObject());
            
            // Check token type - accept both "JWT" and "at+jwt"
            String tokenType = (String) jwt.getHeader().toJSONObject().get("typ");
            System.out.println("Token type: " + tokenType);
            
            if (tokenType != null && 
                !"JWT".equalsIgnoreCase(tokenType) && 
                !"at+jwt".equalsIgnoreCase(tokenType)) {
                System.out.println("Warning: Unexpected token type: " + tokenType + " (proceeding anyway)");
            }
            
            // Convert to Spring Security Jwt
            Map<String, Object> headers = new LinkedHashMap<>(jwt.getHeader().toJSONObject());
            Map<String, Object> claims = new LinkedHashMap<>(claimsSet.toJSONObject());
            
            // Handle time claims
            Instant issuedAt = claimsSet.getIssueTime() != null ? claimsSet.getIssueTime().toInstant() : null;
            Instant expiresAt = claimsSet.getExpirationTime() != null ? claimsSet.getExpirationTime().toInstant() : null;
            
            // Create Spring Security JWT
            Jwt springJwt = new Jwt(token, issuedAt, expiresAt, headers, claims);
            
            System.out.println("Spring Security JWT created successfully");
            System.out.println("Final JWT headers: " + springJwt.getHeaders());
            System.out.println("Final JWT claims: " + springJwt.getClaims());
            
            // Validate the JWT
            OAuth2TokenValidatorResult result = jwtValidator.validate(springJwt);
            if (result.hasErrors()) {
                System.out.println("JWT validation failed: " + result.getErrors());
                throw new BadJwtException("JWT validation failed: " + result.getErrors());
            }
            
            System.out.println("JWT validation successful");
            System.out.println("=====================================");
            
            return springJwt;
            
        } catch (Exception e) {
            System.out.println("JWT decoding failed: " + e.getMessage());
            e.printStackTrace();
            throw new JwtException("JWT decoding failed", e);
        }
    }
}
