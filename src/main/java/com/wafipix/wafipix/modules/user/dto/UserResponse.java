package com.wafipix.wafipix.modules.user.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.wafipix.wafipix.modules.user.enums.AuthProvider;
import com.wafipix.wafipix.modules.user.enums.UserRole;

/**
 * Response DTO for user data
 * Using record for immutability and simplicity
 */
public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        UserRole role,
        AuthProvider authProvider,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    
}
