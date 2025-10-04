package com.wafipix.wafipix.common.security.service.impl;

import com.wafipix.wafipix.common.config.AdminConfigProperties;
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
    private final AdminConfigProperties adminConfig;
    
    private static final UserRole ADMIN_ROLE = UserRole.ADMIN;
    private static final AuthProvider ADMIN_AUTH_PROVIDER = AuthProvider.CUSTOM;
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        try {
            // Check if admin initialization is enabled
            if (!adminConfig.isEnabled()) {
                log.info("Admin user initialization is disabled via configuration");
                return;
            }
            
            log.info("Starting runtime admin user initialization...");
            
            // Process each admin user from configuration
            for (AdminConfigProperties.AdminUser adminUserConfig : adminConfig.getUsers()) {
                if (adminUserConfig.getEmail() == null || adminUserConfig.getEmail().trim().isEmpty()) {
                    log.warn("Skipping admin user with empty email");
                    continue;
                }
                
                processAdminUser(adminUserConfig);
            }
            
            log.info("Runtime admin user initialization completed successfully");
            
        } catch (Exception e) {
            log.error("Error during runtime admin user initialization: {}", e.getMessage(), e);
            // Don't throw exception to prevent application startup failure
        }
    }
    
    /**
     * Process a single admin user (create or update)
     */
    private void processAdminUser(AdminConfigProperties.AdminUser adminUserConfig) {
        String adminEmail = adminUserConfig.getEmail().trim();
        
        // Check if admin user already exists
        Optional<User> existingAdmin = userRepository.findByEmailIgnoreCase(adminEmail);
        
        if (existingAdmin.isPresent()) {
            User admin = existingAdmin.get();
            log.info("Admin user already exists: {} (ID: {})", admin.getEmail(), admin.getId());
            
            // Update admin user if needed
            updateAdminUserIfNeeded(admin, adminUserConfig);
        } else {
            // Create new admin user
            createAdminUser(adminUserConfig);
        }
    }
    
    /**
     * Create new admin user
     */
    private void createAdminUser(AdminConfigProperties.AdminUser adminUserConfig) {
        try {
            log.info("Creating new admin user: {}", adminUserConfig.getEmail());
            
            User adminUser = User.builder()
                    .email(adminUserConfig.getEmail())
                    .firstName(adminUserConfig.getFirstName())
                    .lastName(adminUserConfig.getLastName())
                    .phone(adminUserConfig.getPhone())
                    .role(ADMIN_ROLE)
                    .authProvider(ADMIN_AUTH_PROVIDER)
                    .password(null) // No password for OTP-based auth
                    .providerId(null) // No OAuth provider ID
                    .isActive(adminUserConfig.isActive())
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
    private void updateAdminUserIfNeeded(User admin, AdminConfigProperties.AdminUser adminUserConfig) {
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
        if (adminUserConfig.isActive() && !admin.getIsActive()) {
            log.info("Activating admin user");
            admin.setIsActive(true);
            needsUpdate = true;
        }
        
        // Check if name needs update
        if (!adminUserConfig.getFirstName().equals(admin.getFirstName()) || 
            !adminUserConfig.getLastName().equals(admin.getLastName())) {
            log.info("Updating admin user name from {} {} to {} {}", 
                    admin.getFirstName(), admin.getLastName(), 
                    adminUserConfig.getFirstName(), adminUserConfig.getLastName());
            admin.setFirstName(adminUserConfig.getFirstName());
            admin.setLastName(adminUserConfig.getLastName());
            needsUpdate = true;
        }
        
        // Check if phone needs update
        if (adminUserConfig.getPhone() != null && 
            !adminUserConfig.getPhone().equals(admin.getPhone())) {
            log.info("Updating admin user phone from {} to {}", 
                    admin.getPhone(), adminUserConfig.getPhone());
            admin.setPhone(adminUserConfig.getPhone());
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
