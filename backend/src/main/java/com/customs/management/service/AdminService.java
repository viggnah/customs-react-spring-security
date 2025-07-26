package com.customs.management.service;

import com.customs.management.dto.UserDto;
import com.customs.management.entity.Role;
import com.customs.management.entity.RoleName;
import com.customs.management.entity.User;
import com.customs.management.repository.UserRepository;
import com.customs.management.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::convertToUserDto);
    }

    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToUserDto);
    }

    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToUserDto);
    }

    public Optional<UserDto> updateUserStatus(Long userId, boolean enabled) {
        return userRepository.findById(userId)
                .map(user -> {
                    user.setEnabled(enabled);
                    User updatedUser = userRepository.save(user);
                    return convertToUserDto(updatedUser);
                });
    }

    public Optional<UserDto> updateUserRoles(Long userId, Set<String> roleNames) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        Set<Role> roles = new HashSet<>();
        
        for (String roleName : roleNames) {
            try {
                RoleName roleEnum = RoleName.valueOf(roleName);
                Optional<Role> roleOpt = roleRepository.findByName(roleEnum);
                if (roleOpt.isPresent()) {
                    roles.add(roleOpt.get());
                }
            } catch (IllegalArgumentException e) {
                // Role name not found in enum, skip it
            }
        }
        
        user.setRoles(roles);
        User updatedUser = userRepository.save(user);
        return Optional.of(convertToUserDto(updatedUser));
    }

    public boolean deleteUser(Long userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return true;
        }
        return false;
    }

    public UserDto createUser(String username, String email, String firstName, String lastName, 
                             String password, Set<String> roleNames) {
        
        // Check if username already exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);

        // Set roles
        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
            try {
                RoleName roleEnum = RoleName.valueOf(roleName);
                Optional<Role> roleOpt = roleRepository.findByName(roleEnum);
                if (roleOpt.isPresent()) {
                    roles.add(roleOpt.get());
                }
            } catch (IllegalArgumentException e) {
                // Role name not found in enum, skip it
            }
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return convertToUserDto(savedUser);
    }

    private UserDto convertToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEnabled(user.getEnabled());
        
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());
        dto.setRoles(roleNames);
        
        return dto;
    }
}
