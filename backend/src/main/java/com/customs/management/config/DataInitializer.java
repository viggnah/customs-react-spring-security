package com.customs.management.config;

import com.customs.management.entity.*;
import com.customs.management.repository.AuthorityRepository;
import com.customs.management.repository.RoleRepository;
import com.customs.management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private AuthorityRepository authorityRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeAuthorities();
        initializeRoles();
        initializeUsers();
    }
    
    private void initializeAuthorities() {
        for (AuthorityName authorityName : AuthorityName.values()) {
            if (!authorityRepository.existsByName(authorityName)) {
                Authority authority = new Authority(authorityName, authorityName.getDescription(), authorityName.getCategory());
                authorityRepository.save(authority);
            }
        }
    }
    
    private void initializeRoles() {
        // Admin Role
        if (!roleRepository.existsByName(RoleName.ADMIN)) {
            Role adminRole = new Role(RoleName.ADMIN, "Administrator with full system access");
            Set<Authority> adminAuthorities = new HashSet<>(authorityRepository.findAll());
            adminRole.setAuthorities(adminAuthorities);
            roleRepository.save(adminRole);
        }
        
        // Customs Officer Role
        if (!roleRepository.existsByName(RoleName.CUSTOMS_OFFICER)) {
            Role customsOfficerRole = new Role(RoleName.CUSTOMS_OFFICER, "General customs operations officer");
            Set<Authority> customsAuthorities = getAuthoritiesByNames(
                AuthorityName.READ_CARGO, AuthorityName.UPDATE_CARGO, AuthorityName.INSPECT_CARGO,
                AuthorityName.READ_VEHICLE, AuthorityName.UPDATE_VEHICLE, AuthorityName.INSPECT_VEHICLE,
                AuthorityName.CALCULATE_DUTY, AuthorityName.PROCESS_PAYMENT, AuthorityName.VIEW_REPORTS
            );
            customsOfficerRole.setAuthorities(customsAuthorities);
            roleRepository.save(customsOfficerRole);
        }
        
        // Cargo Inspector Role
        if (!roleRepository.existsByName(RoleName.CARGO_INSPECTOR)) {
            Role cargoInspectorRole = new Role(RoleName.CARGO_INSPECTOR, "Cargo inspection specialist");
            Set<Authority> cargoAuthorities = getAuthoritiesByNames(
                AuthorityName.READ_CARGO, AuthorityName.UPDATE_CARGO, AuthorityName.INSPECT_CARGO,
                AuthorityName.APPROVE_CARGO, AuthorityName.REJECT_CARGO, AuthorityName.VIEW_REPORTS
            );
            cargoInspectorRole.setAuthorities(cargoAuthorities);
            roleRepository.save(cargoInspectorRole);
        }
        
        // Vehicle Inspector Role
        if (!roleRepository.existsByName(RoleName.VEHICLE_INSPECTOR)) {
            Role vehicleInspectorRole = new Role(RoleName.VEHICLE_INSPECTOR, "Vehicle importation specialist");
            Set<Authority> vehicleAuthorities = getAuthoritiesByNames(
                AuthorityName.READ_VEHICLE, AuthorityName.UPDATE_VEHICLE, AuthorityName.INSPECT_VEHICLE,
                AuthorityName.APPROVE_VEHICLE, AuthorityName.REJECT_VEHICLE, AuthorityName.VIEW_REPORTS
            );
            vehicleInspectorRole.setAuthorities(vehicleAuthorities);
            roleRepository.save(vehicleInspectorRole);
        }
        
        // Duty Officer Role
        if (!roleRepository.existsByName(RoleName.DUTY_OFFICER)) {
            Role dutyOfficerRole = new Role(RoleName.DUTY_OFFICER, "Duty calculation and management specialist");
            Set<Authority> dutyAuthorities = getAuthoritiesByNames(
                AuthorityName.CALCULATE_DUTY, AuthorityName.APPROVE_DUTY, AuthorityName.PROCESS_PAYMENT,
                AuthorityName.REFUND_DUTY, AuthorityName.VIEW_REPORTS, AuthorityName.GENERATE_REPORTS
            );
            dutyOfficerRole.setAuthorities(dutyAuthorities);
            roleRepository.save(dutyOfficerRole);
        }
    }
    
    private void initializeUsers() {
        // Admin User
        if (!userRepository.existsByUsername("admin.customs")) {
            User admin = new User("admin.customs", passwordEncoder.encode("admin123"), "admin@customs.gov", "Admin", "User");
            admin.setRoles(Set.of(roleRepository.findByName(RoleName.ADMIN).get()));
            userRepository.save(admin);
        }
        
        // Customs Officer
        if (!userRepository.existsByUsername("john.smith")) {
            User customsOfficer = new User("john.smith", passwordEncoder.encode("customs123"), "john.smith@customs.gov", "John", "Smith");
            customsOfficer.setRoles(Set.of(roleRepository.findByName(RoleName.CUSTOMS_OFFICER).get()));
            userRepository.save(customsOfficer);
        }
        
        // Cargo Inspector
        if (!userRepository.existsByUsername("jane.doe")) {
            User cargoInspector = new User("jane.doe", passwordEncoder.encode("cargo123"), "jane.doe@customs.gov", "Jane", "Doe");
            cargoInspector.setRoles(Set.of(roleRepository.findByName(RoleName.CARGO_INSPECTOR).get()));
            userRepository.save(cargoInspector);
        }
        
        // Vehicle Inspector
        if (!userRepository.existsByUsername("mike.wilson")) {
            User vehicleInspector = new User("mike.wilson", passwordEncoder.encode("vehicle123"), "mike.wilson@customs.gov", "Mike", "Wilson");
            vehicleInspector.setRoles(Set.of(roleRepository.findByName(RoleName.VEHICLE_INSPECTOR).get()));
            userRepository.save(vehicleInspector);
        }
    }
    
    private Set<Authority> getAuthoritiesByNames(AuthorityName... authorityNames) {
        Set<Authority> authorities = new HashSet<>();
        for (AuthorityName authorityName : authorityNames) {
            authorityRepository.findByName(authorityName).ifPresent(authorities::add);
        }
        return authorities;
    }
}
