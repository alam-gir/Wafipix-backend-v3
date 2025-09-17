package com.wafipix.wafipix.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

@Configuration
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // Fetch the current user from the security context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();

                // Handle different types of principals
                if (principal instanceof UserDetails) {
                    return Optional.of(((UserDetails) principal).getUsername());
                } else if (principal instanceof OAuth2User) {
                    // Extract email or preferred username for OAuth2 users
                    String email = ((OAuth2User) principal).getAttribute("email");
                    return Optional.ofNullable(email);
                } else if (principal instanceof String) {
                    // For cases where principal might be a simple username string
                    return Optional.of(principal.toString());
                }
            }
            return Optional.of("SYSTEM"); // Default value if no user is logged in
        };
    }
}
