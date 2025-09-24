package com.wafipix.wafipix.common.security.service;

import com.wafipix.wafipix.common.security.dto.SendOtpRequest;
import com.wafipix.wafipix.common.security.dto.VerifyOtpRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

/**
 * Service interface for admin/employee authentication using OTP
 */
public interface AdminAuthService {
    
    /**
     * Send OTP code to admin/employee email
     * @param request Send OTP request containing email and device ID
     * @param ipAddress Client IP address
     * @return Success response
     */
    ResponseEntity<?> sendOtp(SendOtpRequest request, String ipAddress);
    
    /**
     * Verify OTP and complete login
     * @param request Verify OTP request containing email, OTP code, and device ID
     * @param response HTTP response for setting cookies
     * @return Login response with JWT tokens and user info
     */
    ResponseEntity<?> verifyOtpAndLogin(VerifyOtpRequest request, HttpServletResponse response);
}
