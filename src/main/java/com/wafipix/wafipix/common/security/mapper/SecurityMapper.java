package com.wafipix.wafipix.common.security.mapper;

import com.wafipix.wafipix.common.security.dto.AdminProfileResponse;
import com.wafipix.wafipix.modules.user.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper for security-related profile responses
 * Centralizes profile mapping logic to avoid duplication
 */
@Component
public class SecurityMapper {
    
    /**
     * Maps User entity to AdminProfileResponse
     * Used for both admin/employee and regular user profile responses
     * 
     * @param user User entity
     * @return AdminProfileResponse
     */
    public AdminProfileResponse toProfileResponse(User user) {
        if (user == null) {
            return null;
        }
        
        return AdminProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
