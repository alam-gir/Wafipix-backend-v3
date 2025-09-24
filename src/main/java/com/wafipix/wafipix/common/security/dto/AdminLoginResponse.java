package com.wafipix.wafipix.common.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for successful admin/employee login
 * Contains JWT tokens and user information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginResponse {
    
    private String accessToken;
    private String refreshToken;
}
