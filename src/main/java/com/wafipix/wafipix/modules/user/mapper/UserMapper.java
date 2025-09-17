package com.wafipix.wafipix.modules.user.mapper;

import com.wafipix.wafipix.modules.user.entity.User;
import org.springframework.stereotype.Component;

/**
 * Simple User Mapper
 */
@Component
public class UserMapper {
    
    /**
     * Create User entity from OAuth2 data
     */
    public User toEntity(String name, String email, String picture, String role) {
        // Parse name into firstName and lastName
        String[] nameParts = name != null ? name.split(" ", 2) : new String[]{"", ""};
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        return User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .providerId(picture) // Store picture URL in providerId field
                .role(role)
                .isActive(true)
                .build();
    }
}
