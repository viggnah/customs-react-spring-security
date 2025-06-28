package com.tra.customs.dto;

import java.util.Set;

public class JwtResponse {
    
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private Set<String> roles;
    private Set<String> authorities;
    
    // Constructors
    public JwtResponse() {}
    
    public JwtResponse(String token, String username, String email, Set<String> roles, Set<String> authorities) {
        this.token = token;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.authorities = authorities;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    
    public Set<String> getAuthorities() {
        return authorities;
    }
    
    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }
}
