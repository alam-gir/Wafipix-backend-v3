package com.wafipix.wafipix.common.security.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for verifying OTP and completing admin/employee login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP code must be 6 digits")
    private String otpCode;
    
    @NotBlank(message = "Device ID is required")
    private String deviceId;
}
