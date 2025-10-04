package com.wafipix.wafipix.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;

/**
 * Configuration properties for admin user initialization
 */
@Data
@Component
@ConfigurationProperties(prefix = "admin")
public class AdminConfigProperties {
    
    /**
     * List of admin users to initialize
     */
    private List<AdminUser> users = new ArrayList<>();
    
    /**
     * Whether to enable automatic admin creation
     */
    private boolean enabled = true;
    
    @Data
    public static class AdminUser {
        /**
         * Admin email address
         */
        private String email;
        
        /**
         * Admin first name
         */
        private String firstName;
        
        /**
         * Admin last name
         */
        private String lastName;
        
        /**
         * Admin phone number (optional)
         */
        private String phone;
        
        /**
         * Whether the admin user should be active
         */
        private boolean active = true;
    }
}
