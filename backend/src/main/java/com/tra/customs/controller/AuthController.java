package com.tra.customs.controller;

import com.tra.customs.dto.JwtResponse;
import com.tra.customs.dto.LoginRequest;
import com.tra.customs.dto.PasswordResetRequest;
import com.tra.customs.entity.User;
import com.tra.customs.service.AuthService;
import com.tra.customs.util.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Profile("!wso2")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            
            // Update last login
            authService.updateLastLogin(loginRequest.getUsername());
            
            // Get user details
            User user = authService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
            
            Set<String> authorities = user.getRoles().stream()
                .flatMap(role -> role.getAuthorities().stream())
                .map(authority -> authority.getName().name())
                .collect(Collectors.toSet());
            
            return ResponseEntity.ok(new JwtResponse(jwt, user.getUsername(), user.getEmail(), roles, authorities));
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        try {
            String token = authService.createPasswordResetToken(passwordResetRequest.getUsername());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset instructions have been sent to your email address.");
            response.put("token", token); // For demo purposes - remove in production
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found with username: " + passwordResetRequest.getUsername());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        try {
            if (!authService.validatePasswordResetToken(token)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid or expired password reset token");
                return ResponseEntity.badRequest().body(error);
            }
            
            authService.resetPassword(token, newPassword);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password has been reset successfully. You can now log in with your new password.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean isValid = authService.validatePasswordResetToken(token);
        
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", isValid);
        
        return ResponseEntity.ok(response);
    }
}
