package com.wafipix.wafipix.common.security.service;

/**
 * Service interface for OTP code management
 * Handles OTP generation, validation, and cleanup
 */
public interface OtpService {
    
    /**
     * Generate and send OTP code to email
     * @param email Recipient email address
     * @param deviceId Device identifier
     * @param ipAddress Client IP address
     * @return true if OTP sent successfully, false otherwise
     */
    boolean generateAndSendOtp(String email, String deviceId, String ipAddress);
    
    /**
     * Validate OTP code for email
     * @param email User email
     * @param code OTP code to validate
     * @param deviceId Device identifier
     * @return true if OTP is valid, false otherwise
     */
    boolean validateOtp(String email, String code, String deviceId);
    
    /**
     * Clean up expired OTP codes
     * @return Number of expired OTPs cleaned up
     */
    int cleanupExpiredOtps();
    
    /**
     * Check if email has reached rate limit
     * @param email User email
     * @return true if rate limit reached, false otherwise
     */
    boolean isRateLimitReached(String email);
    
    /**
     * Get remaining attempts for OTP
     * @param email User email
     * @param code OTP code
     * @return Number of remaining attempts, or -1 if OTP not found
     */
    int getRemainingAttempts(String email, String code);
}
