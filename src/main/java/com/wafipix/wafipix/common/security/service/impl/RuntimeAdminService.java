package com.wafipix.wafipix.common.security.service.impl;

import com.wafipix.wafipix.modules.user.entity.User;
import com.wafipix.wafipix.modules.user.enums.AuthProvider;
import com.wafipix.wafipix.modules.user.enums.UserRole;
import com.wafipix.wafipix.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service to initialize admin user at runtime
 * Creates the default admin user if it doesn't exist
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RuntimeAdminService implements CommandLineRunner {
    
    private final UserRepository userRepository;
    
    // Admin user details
    private static final String ADMIN_EMAIL = "info.alamgirhussain@gmail.com";
    private static final String ADMIN_FIRST_NAME = "Alamgir";
    private static final String ADMIN_LAST_NAME = "Hussain";
    private static final UserRole ADMIN_ROLE = UserRole.ADMIN;
    private static final AuthProvider ADMIN_AUTH_PROVIDER = AuthProvider.CUSTOM;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            log.info("Starting runtime admin user initialization...");
            
            // Check if admin user already exists
            Optional<User> existingAdmin = userRepository.findByEmailIgnoreCase(ADMIN_EMAIL);
            
            if (existingAdmin.isPresent()) {
                User admin = existingAdmin.get();
                log.info("Admin user already exists: {} (ID: {})", admin.getEmail(), admin.getId());
                
                // Update admin user if needed
                updateAdminUserIfNeeded(admin);
            } else {
                // Create new admin user
                createAdminUser();
            }
            
            log.info("Runtime admin user initialization completed successfully");
            
        } catch (Exception e) {
            log.error("Error during runtime admin user initialization: {}", e.getMessage(), e);
            // Don't throw exception to prevent application startup failure
        }
    }
    
    /**
     * Create new admin user
     */
    private void createAdminUser() {
        try {
            log.info("Creating new admin user: {}", ADMIN_EMAIL);
            
            User adminUser = User.builder()
                    .email(ADMIN_EMAIL)
                    .firstName(ADMIN_FIRST_NAME)
                    .lastName(ADMIN_LAST_NAME)
                    .role(ADMIN_ROLE)
                    .authProvider(ADMIN_AUTH_PROVIDER)
                    .password(null) // No password for OTP-based auth
                    .providerId(null) // No OAuth provider ID
                    .isActive(true)
                    .build();
            
            User savedAdmin = userRepository.save(adminUser);
            
            log.info("Successfully created admin user: {} (ID: {})", savedAdmin.getEmail(), savedAdmin.getId());
            
        } catch (Exception e) {
            log.error("Error creating admin user: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Update admin user if needed
     */
    private void updateAdminUserIfNeeded(User admin) {
        boolean needsUpdate = false;
        
        // Check if role needs update
        if (!ADMIN_ROLE.equals(admin.getRole())) {
            log.info("Updating admin user role from {} to {}", admin.getRole(), ADMIN_ROLE);
            admin.setRole(ADMIN_ROLE);
            needsUpdate = true;
        }
        
        // Check if auth provider needs update
        if (!ADMIN_AUTH_PROVIDER.equals(admin.getAuthProvider())) {
            log.info("Updating admin user auth provider from {} to {}", admin.getAuthProvider(), ADMIN_AUTH_PROVIDER);
            admin.setAuthProvider(ADMIN_AUTH_PROVIDER);
            needsUpdate = true;
        }
        
        // Check if user is active
        if (!admin.getIsActive()) {
            log.info("Activating admin user");
            admin.setIsActive(true);
            needsUpdate = true;
        }
        
        // Check if name needs update
        if (!ADMIN_FIRST_NAME.equals(admin.getFirstName()) || !ADMIN_LAST_NAME.equals(admin.getLastName())) {
            log.info("Updating admin user name from {} {} to {} {}", 
                    admin.getFirstName(), admin.getLastName(), ADMIN_FIRST_NAME, ADMIN_LAST_NAME);
            admin.setFirstName(ADMIN_FIRST_NAME);
            admin.setLastName(ADMIN_LAST_NAME);
            needsUpdate = true;
        }
        
        // Save updates if needed
        if (needsUpdate) {
            try {
                userRepository.save(admin);
                log.info("Successfully updated admin user: {}", admin.getEmail());
            } catch (Exception e) {
                log.error("Error updating admin user: {}", e.getMessage(), e);
                throw e;
            }
        } else {
            log.info("Admin user is up to date: {}", admin.getEmail());
        }
    }
}
