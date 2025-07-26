package com.customs.management.dto;

import java.util.List;

public class UserMenuResponse {
    private List<MenuItemDto> menuItems;
    private List<String> authorities;

    // Constructors
    public UserMenuResponse() {}

    public UserMenuResponse(List<MenuItemDto> menuItems, List<String> authorities) {
        this.menuItems = menuItems;
        this.authorities = authorities;
    }

    // Getters and Setters
    public List<MenuItemDto> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItemDto> menuItems) {
        this.menuItems = menuItems;
    }

    public List<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<String> authorities) {
        this.authorities = authorities;
    }
}
