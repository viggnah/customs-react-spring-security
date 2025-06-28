package com.tra.customs.controller;

import com.tra.customs.dto.UserDto;
import com.tra.customs.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('READ_USER')")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserDto> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('READ_USER')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        Optional<UserDto> user = adminService.getUserById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/username/{username}")
    @PreAuthorize("hasAuthority('READ_USER')")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        Optional<UserDto> user = adminService.getUserByUsername(username);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> request) {
        try {
            String username = (String) request.get("username");
            String email = (String) request.get("email");
            String firstName = (String) request.get("firstName");
            String lastName = (String) request.get("lastName");
            String password = (String) request.get("password");
            @SuppressWarnings("unchecked")
            Set<String> roleNames = (Set<String>) request.get("roles");

            UserDto createdUser = adminService.createUser(username, email, firstName, lastName, password, roleNames);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid request"));
        }
    }

    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public ResponseEntity<UserDto> updateUserStatus(
            @PathVariable Long id, 
            @RequestParam boolean enabled) {
        Optional<UserDto> updatedUser = adminService.updateUserStatus(id, enabled);
        return updatedUser.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/users/{id}/roles")
    @PreAuthorize("hasAuthority('MANAGE_ROLES')")
    public ResponseEntity<UserDto> updateUserRoles(
            @PathVariable Long id, 
            @RequestBody Set<String> roleNames) {
        Optional<UserDto> updatedUser = adminService.updateUserRoles(id, roleNames);
        return updatedUser.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean deleted = adminService.deleteUser(id);
        if (deleted) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
