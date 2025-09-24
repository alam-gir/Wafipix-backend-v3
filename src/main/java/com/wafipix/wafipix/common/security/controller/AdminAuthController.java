package com.wafipix.wafipix.common.security.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.common.security.dto.SendOtpRequest;
import com.wafipix.wafipix.common.security.dto.VerifyOtpRequest;
import com.wafipix.wafipix.common.security.dto.AdminProfileResponse;
import com.wafipix.wafipix.common.security.service.AdminAuthService;
import com.wafipix.wafipix.modules.user.entity.User;
import com.wafipix.wafipix.modules.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for admin/employee authentication using OTP
 */
@RestController
@RequestMapping("/v3/admin/auth")
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {
    
    private final AdminAuthService adminAuthService;
    private final UserRepository userRepository;
    
    /**
     * Send OTP code to admin/employee email
     * @param request Send OTP request containing email and device ID
     * @param httpRequest HTTP request for getting client IP
     * @return Success response
     */
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody SendOtpRequest request, HttpServletRequest httpRequest) {
        try {
            log.info("Received send OTP request for email: {}", request.getEmail());
            
            String clientIp = getClientIpAddress(httpRequest);
            return adminAuthService.sendOtp(request, clientIp);
            
        } catch (Exception e) {
            log.error("Error in send OTP endpoint: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Verify OTP and complete admin/employee login
     * @param request Verify OTP request containing email, OTP code, and device ID
     * @param response HTTP response for setting cookies
     * @return Login response with JWT tokens and user info
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtpAndLogin(@Valid @RequestBody VerifyOtpRequest request, HttpServletResponse response) {
        try {
            log.info("Received verify OTP request for email: {}", request.getEmail());
            
            return adminAuthService.verifyOtpAndLogin(request, response);
            
        } catch (Exception e) {
            log.error("Error in verify OTP endpoint: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Get client IP address from request
     * Handles various proxy headers for accurate IP detection
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        String xForwarded = request.getHeader("X-Forwarded");
        if (xForwarded != null && !xForwarded.isEmpty() && !"unknown".equalsIgnoreCase(xForwarded)) {
            return xForwarded;
        }
        
        String forwardedFor = request.getHeader("Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(forwardedFor)) {
            return forwardedFor;
        }
        
        String forwarded = request.getHeader("Forwarded");
        if (forwarded != null && !forwarded.isEmpty() && !"unknown".equalsIgnoreCase(forwarded)) {
            return forwarded;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Get current admin/employee profile information
     * @return Admin profile information
     */
    @GetMapping("/me")
    public ResponseEntity<?> getProfile() {
        try {
            log.info("Received get profile request");
            
            // Get current authentication
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || authentication.getName() == null) {
                log.warn("No authentication found for profile request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.builder()
                                .success(false)
                                .message("Authentication required")
                                .statusCode(HttpStatus.UNAUTHORIZED.value())
                                .build());
            }
            
            String email = authentication.getName();
            log.debug("Getting profile for authenticated user: {}", email);
            
            // Find user by email
            User user = userRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Verify user is admin or employee
            if (!isAdminOrEmployee(user)) {
                log.warn("Non-admin/employee user attempted to access profile: {}", email);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(ApiResponse.builder()
                                .success(false)
                                .message("Access denied. Admin/Employee access required")
                                .statusCode(HttpStatus.FORBIDDEN.value())
                                .build());
            }
            
            // Create profile response
            AdminProfileResponse profileResponse = AdminProfileResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .phone(user.getPhone())
                    .phone(user.getPhone())
                    .role(user.getRole().name())
                    .isActive(user.getIsActive())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
            
            log.info("Successfully retrieved profile for user: {}", email);
            
            ApiResponse<AdminProfileResponse> response = ApiResponse.<AdminProfileResponse>builder()
                    .success(true)
                    .message("Profile retrieved successfully")
                    .data(profileResponse)
                    .statusCode(HttpStatus.OK.value())
                    .build();
            
            return new ResponseEntity<>(response, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error in get profile endpoint: {}", e.getMessage(), e);
            
            ApiResponse<Object> errorResponse = ApiResponse.builder()
                    .success(false)
                    .message("Failed to retrieve profile: " + e.getMessage())
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();
            
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Check if user is admin or employee
     */
    private boolean isAdminOrEmployee(User user) {
        return user.getRole() == com.wafipix.wafipix.modules.user.enums.UserRole.ADMIN ||
               user.getRole() == com.wafipix.wafipix.modules.user.enums.UserRole.SUPPORT ||
               user.getRole() == com.wafipix.wafipix.modules.user.enums.UserRole.DESIGNER;
    }
}
