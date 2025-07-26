package com.customs.management.dto;

import com.customs.management.entity.AuthorityName;

public class MenuItemDto {
    private String id;
    private String label;
    private String path;
    private String icon;
    private AuthorityName requiredAuthority;
    private boolean visible;

    // Constructors
    public MenuItemDto() {}

    public MenuItemDto(String id, String label, String path, String icon, AuthorityName requiredAuthority) {
        this.id = id;
        this.label = label;
        this.path = path;
        this.icon = icon;
        this.requiredAuthority = requiredAuthority;
        this.visible = true;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public AuthorityName getRequiredAuthority() {
        return requiredAuthority;
    }

    public void setRequiredAuthority(AuthorityName requiredAuthority) {
        this.requiredAuthority = requiredAuthority;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
