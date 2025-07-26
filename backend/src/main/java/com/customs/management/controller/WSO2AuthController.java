package com.customs.management.controller;

import com.customs.management.service.WSO2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class WSO2AuthController {

    @Autowired
    private WSO2UserService wso2UserService;

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Invalid token");
        }

        Map<String, Object> userInfo = wso2UserService.extractUserInfo(authentication);
        if (userInfo == null) {
            return ResponseEntity.status(401).body("Unable to extract user information");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("username", userInfo.get("username"));
        response.put("email", userInfo.get("email"));
        response.put("firstName", userInfo.get("firstName"));
        response.put("lastName", userInfo.get("lastName"));
        response.put("fullName", userInfo.get("fullName"));
        response.put("groups", userInfo.get("groups"));
        response.put("roles", userInfo.get("roles"));
        response.put("authorities", userInfo.get("permissions"));
        response.put("authenticated", true);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        Map<String, Object> userInfo = wso2UserService.extractUserInfo(authentication);
        if (userInfo == null) {
            return ResponseEntity.status(401).body("Unable to extract user information");
        }

        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(Authentication authentication) {
        // Note: Token refresh is typically handled by the WSO2 IS directly
        // This endpoint can be used to validate if the current token is still valid
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Token expired or invalid");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Token is still valid");
        response.put("authenticated", true);
        response.put("user", wso2UserService.extractUserInfo(authentication));

        return ResponseEntity.ok(response);
    }
}
