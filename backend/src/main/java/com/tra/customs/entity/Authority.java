package com.tra.customs.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "authorities")
public class Authority {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private AuthorityName name;
    
    @Column(length = 500)
    private String description;
    
    @Column(length = 100)
    private String category;
    
    @ManyToMany(mappedBy = "authorities")
    private Set<Role> roles = new HashSet<>();
    
    // Constructors
    public Authority() {}
    
    public Authority(AuthorityName name, String description, String category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public AuthorityName getName() {
        return name;
    }
    
    public void setName(AuthorityName name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public Set<Role> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
