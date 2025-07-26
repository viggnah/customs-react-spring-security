package com.customs.management.controller;

import com.customs.management.dto.MenuItemDto;
import com.customs.management.dto.UserMenuResponse;
import com.customs.management.entity.AuthorityName;
import com.customs.management.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserMenuResponse> getUserMenu(Authentication authentication) {
        String username = authentication.getName();
        UserMenuResponse menuResponse = menuService.getUserMenu(username);
        return ResponseEntity.ok(menuResponse);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public ResponseEntity<List<MenuItemDto>> getAllMenuItems() {
        List<MenuItemDto> menuItems = menuService.getAllMenuItems();
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/by-authority/{authority}")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public ResponseEntity<List<MenuItemDto>> getMenuItemsByAuthority(@PathVariable AuthorityName authority) {
        List<MenuItemDto> menuItems = menuService.getMenuItemsByAuthority(authority);
        return ResponseEntity.ok(menuItems);
    }
}
