package com.customs.management.dto;

import jakarta.validation.constraints.NotBlank;

public class PasswordResetRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    // Constructors
    public PasswordResetRequest() {}
    
    public PasswordResetRequest(String username) {
        this.username = username;
    }
    
    // Getters and Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
}
