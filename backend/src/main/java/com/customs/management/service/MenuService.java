package com.customs.management.service;

import com.customs.management.dto.MenuItemDto;
import com.customs.management.dto.UserMenuResponse;
import com.customs.management.entity.Authority;
import com.customs.management.entity.AuthorityName;
import com.customs.management.entity.User;
import com.customs.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private UserRepository userRepository;

    private static final List<MenuItemDto> ALL_MENU_ITEMS = List.of(
        new MenuItemDto("dashboard", "Dashboard", "/dashboard", "dashboard", null),
        new MenuItemDto("cargo", "Cargo Management", "/cargo", "cargo", AuthorityName.READ_CARGO),
        new MenuItemDto("cargo-create", "New Cargo Entry", "/cargo/new", "plus", AuthorityName.CREATE_CARGO),
        new MenuItemDto("cargo-inspect", "Cargo Inspection", "/cargo/inspect", "inspect", AuthorityName.INSPECT_CARGO),
        new MenuItemDto("vehicle", "Vehicle Import", "/vehicles", "vehicle", AuthorityName.READ_VEHICLE),
        new MenuItemDto("vehicle-create", "New Vehicle Import", "/vehicles/new", "plus", AuthorityName.CREATE_VEHICLE),
        new MenuItemDto("vehicle-inspect", "Vehicle Inspection", "/vehicles/inspect", "inspect", AuthorityName.INSPECT_VEHICLE),
        new MenuItemDto("duty", "Duty Management", "/duty", "payment", AuthorityName.CALCULATE_DUTY),
        new MenuItemDto("reports", "Reports", "/reports", "reports", AuthorityName.VIEW_REPORTS),
        new MenuItemDto("admin", "Administration", "/admin", "admin", AuthorityName.MANAGE_ROLES),
        new MenuItemDto("admin-users", "User Management", "/admin/users", "users", AuthorityName.READ_USER),
        new MenuItemDto("admin-roles", "Role Management", "/admin/roles", "roles", AuthorityName.MANAGE_ROLES),
        new MenuItemDto("settings", "Settings", "/settings", "settings", AuthorityName.SYSTEM_CONFIG)
    );

    public UserMenuResponse getUserMenu(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            return new UserMenuResponse(new ArrayList<>(), new ArrayList<>());
        }
        
        User user = userOpt.get();
        Set<AuthorityName> userAuthorities = user.getRoles().stream()
                .flatMap(role -> role.getAuthorities().stream())
                .map(Authority::getName)
                .collect(Collectors.toSet());
        
        List<String> authorityNames = userAuthorities.stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        
        List<MenuItemDto> availableMenuItems = ALL_MENU_ITEMS.stream()
                .filter(menuItem -> menuItem.getRequiredAuthority() == null || 
                                   userAuthorities.contains(menuItem.getRequiredAuthority()))
                .collect(Collectors.toList());
        
        return new UserMenuResponse(availableMenuItems, authorityNames);
    }

    public List<MenuItemDto> getAllMenuItems() {
        return new ArrayList<>(ALL_MENU_ITEMS);
    }

    public List<MenuItemDto> getMenuItemsByAuthority(AuthorityName authority) {
        return ALL_MENU_ITEMS.stream()
                .filter(menuItem -> authority.equals(menuItem.getRequiredAuthority()))
                .collect(Collectors.toList());
    }
}
