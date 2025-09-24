package com.wafipix.wafipix.common.security.service.impl;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.common.exception.AuthenticationException;
import com.wafipix.wafipix.common.exception.AuthorizationException;
import com.wafipix.wafipix.common.exception.BusinessException;
import com.wafipix.wafipix.common.security.dto.AdminLoginResponse;
import com.wafipix.wafipix.common.security.dto.SendOtpRequest;
import com.wafipix.wafipix.common.security.dto.VerifyOtpRequest;
import com.wafipix.wafipix.common.security.enums.TOKEN_TYPE;
import com.wafipix.wafipix.common.security.service.AdminAuthService;
import com.wafipix.wafipix.common.security.service.CookieService;
import com.wafipix.wafipix.common.security.service.JWTService;
import com.wafipix.wafipix.common.security.service.OtpService;
import com.wafipix.wafipix.modules.user.entity.User;
import com.wafipix.wafipix.modules.user.enums.UserRole;
import com.wafipix.wafipix.modules.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAuthServiceImpl implements AdminAuthService {
    
    private final OtpService otpService;
    private final UserRepository userRepository;
    private final JWTService jwtService;
    private final CookieService cookieService;
    
    @Override
    @Transactional
    public ResponseEntity<?> sendOtp(SendOtpRequest request, String ipAddress) {
        try {
            log.info("Processing send OTP request for email: {} with device: {}", request.getEmail(), request.getDeviceId());
            
            // Validate request
            validateSendOtpRequest(request);
            
            // Check if user exists and is admin/employee
            Optional<User> userOpt = userRepository.findByEmailIgnoreCase(request.getEmail());
            if (userOpt.isEmpty()) {
                log.warn("User not found for email: {}", request.getEmail());
                throw new AuthenticationException("User not found. Please contact administrator.");
            }
            
            User user = userOpt.get();
            
            // Check if user is admin or employee
            if (!isAdminOrEmployee(user)) {
                log.warn("User is not admin/employee for email: {}", request.getEmail());
                throw new AuthorizationException("Access denied. Admin/Employee access required.");
            }
            
            // Check if user is active
            if (!user.getIsActive()) {
                log.warn("Inactive user attempted login: {}", request.getEmail());
                throw new AuthenticationException("Account is inactive. Please contact administrator.");
            }
            
            // Generate and send OTP
            boolean otpSent = otpService.generateAndSendOtp(request.getEmail(), request.getDeviceId(), ipAddress);
            
            if (otpSent) {
                log.info("OTP sent successfully to: {}", request.getEmail());
                
                ApiResponse<String> response = ApiResponse.<String>builder()
                        .success(true)
                        .message("OTP sent successfully to your email")
                        .data("OTP sent")
                        .statusCode(HttpStatus.OK.value())
                        .build();
                
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                throw new BusinessException("Failed to send OTP. Please try again.");
            }
            
        } catch (AuthenticationException e) {
            log.error("Authentication error in send OTP: {}", e.getMessage());
            throw e;
        } catch (AuthorizationException e) {
            log.error("Authorization error in send OTP: {}", e.getMessage());
            throw e;
        } catch (BusinessException e) {
            log.error("Business error in send OTP: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in send OTP: {}", e.getMessage(), e);
            throw new BusinessException("Failed to send OTP: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ResponseEntity<?> verifyOtpAndLogin(VerifyOtpRequest request, HttpServletResponse response) {
        try {
            log.info("Processing verify OTP request for email: {} with device: {}", request.getEmail(), request.getDeviceId());
            
            // Validate request
            validateVerifyOtpRequest(request);
            
            // Verify OTP
            boolean otpValid = otpService.validateOtp(request.getEmail(), request.getOtpCode(), request.getDeviceId());
            
            if (!otpValid) {
                log.warn("Invalid OTP for email: {}", request.getEmail());
                throw new AuthenticationException("Invalid or expired OTP code");
            }
            
            // Find user
            Optional<User> userOpt = userRepository.findByEmailIgnoreCase(request.getEmail());
            if (userOpt.isEmpty()) {
                log.warn("User not found during OTP verification: {}", request.getEmail());
                throw new AuthenticationException("User not found");
            }
            
            User user = userOpt.get();
            
            // Double-check user permissions
            if (!isAdminOrEmployee(user)) {
                log.warn("User is not admin/employee during OTP verification: {}", request.getEmail());
                throw new AuthorizationException("Access denied. Admin/Employee access required.");
            }
            
            // Generate JWT tokens
            Map<TOKEN_TYPE, String> tokens = jwtService.generateTokens(user, request.getDeviceId());
            
            // Set cookies
            response.addCookie(cookieService.create("at", tokens.get(TOKEN_TYPE.ACCESS_TOKEN)));
            response.addCookie(cookieService.create("rt", tokens.get(TOKEN_TYPE.REFRESH_TOKEN)));
            
            // Create simple response with only tokens (like OAuth2 flow)
            AdminLoginResponse loginResponse = AdminLoginResponse.builder()
                    .accessToken(tokens.get(TOKEN_TYPE.ACCESS_TOKEN))
                    .refreshToken(tokens.get(TOKEN_TYPE.REFRESH_TOKEN))
                    .build();
            
            log.info("Successfully logged in admin/employee: {} with device: {}", user.getEmail(), request.getDeviceId());
            
            ApiResponse<AdminLoginResponse> apiResponse = ApiResponse.<AdminLoginResponse>builder()
                    .success(true)
                    .message("Login successful")
                    .data(loginResponse)
                    .statusCode(HttpStatus.OK.value())
                    .build();
            
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
            
        } catch (AuthenticationException e) {
            log.error("Authentication error in verify OTP: {}", e.getMessage());
            throw e;
        } catch (AuthorizationException e) {
            log.error("Authorization error in verify OTP: {}", e.getMessage());
            throw e;
        } catch (BusinessException e) {
            log.error("Business error in verify OTP: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error in verify OTP: {}", e.getMessage(), e);
            throw new BusinessException("Failed to complete login: " + e.getMessage());
        }
    }
    
    /**
     * Check if user is admin or employee
     */
    private boolean isAdminOrEmployee(User user) {
        UserRole role = user.getRole();
        return role == UserRole.ADMIN || role == UserRole.SUPPORT || role == UserRole.DESIGNER;
    }
    
    /**
     * Validate send OTP request
     */
    private void validateSendOtpRequest(SendOtpRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BusinessException("Email is required");
        }
        
        if (request.getDeviceId() == null || request.getDeviceId().trim().isEmpty()) {
            throw new BusinessException("Device ID is required");
        }
        
        // Validate email format
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            throw new BusinessException("Invalid email format");
        }
    }
    
    /**
     * Validate verify OTP request
     */
    private void validateVerifyOtpRequest(VerifyOtpRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new BusinessException("Email is required");
        }
        
        if (request.getOtpCode() == null || request.getOtpCode().trim().isEmpty()) {
            throw new BusinessException("OTP code is required");
        }
        
        if (request.getDeviceId() == null || request.getDeviceId().trim().isEmpty()) {
            throw new BusinessException("Device ID is required");
        }
        
        // Validate OTP format
        if (!request.getOtpCode().matches("^\\d{6}$")) {
            throw new BusinessException("OTP code must be 6 digits");
        }
        
        // Validate email format
        if (!request.getEmail().matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            throw new BusinessException("Invalid email format");
        }
    }
}
