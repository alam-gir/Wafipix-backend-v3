package com.wafipix.wafipix.common.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for admin/employee profile information
 * Contains complete user profile data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProfileResponse {
    
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

