package com.wafipix.wafipix.modules.user.mapper;

import org.springframework.stereotype.Component;

import com.wafipix.wafipix.modules.user.dto.UserResponse;
import com.wafipix.wafipix.modules.user.entity.User;
import com.wafipix.wafipix.modules.user.enums.AuthProvider;
import com.wafipix.wafipix.modules.user.enums.UserRole;

/**
 * Mapper for User entity and DTOs conversion
 * Keeps service layer clean by handling all mapping logic
 */
@Component
public class UserMapper {
    
    /**
     * Create User entity from OAuth data (used by SuccessHandler for Google/Facebook)
     */
    public User toEntity(String name, String email, String picture, String role, AuthProvider authProvider) {
        String[] nameParts = name != null ? name.split(" ", 2) : new String[]{"", ""};
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        return User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .role(UserRole.valueOf(role != null ? role : "CUSTOMER"))
                .authProvider(authProvider)
                .providerId(email) // Using email as provider ID
                .isActive(true)
                .build();
    }
    
    /**
     * Map User entity to UserResponse DTO
     */
    public UserResponse toResponse(User user) {
        return new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhone(),
            user.getRole(),
            user.getAuthProvider(),
            user.getIsActive(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
    
    /**
     * Update User entity with provided data
     */
    public void updateEntity(User user, String email, String firstName, String lastName, String phone) {
        if (email != null && !email.trim().isEmpty()) {
            user.setEmail(email);
        }
        
        if (firstName != null && !firstName.trim().isEmpty()) {
            user.setFirstName(firstName);
        }
        
        if (lastName != null && !lastName.trim().isEmpty()) {
            user.setLastName(lastName);
        }
        
        if (phone != null && !phone.trim().isEmpty()) {
            user.setPhone(phone);
        }
    }
}
