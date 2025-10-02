package com.wafipix.wafipix.common.security.service.impl;

import com.wafipix.wafipix.common.exception.BusinessException;
import com.wafipix.wafipix.common.security.entity.OtpCode;
import com.wafipix.wafipix.common.security.repository.OtpCodeRepository;
import com.wafipix.wafipix.common.security.event.OtpEmailSentEvent;
import com.wafipix.wafipix.common.security.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {
    
    private final OtpCodeRepository otpCodeRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    @Value("${otp.expiration-minutes:10}")
    private int otpExpirationMinutes;
    
    @Value("${otp.rate-limit-per-hour:5}")
    private int rateLimitPerHour;
    
    private static final Random random = new Random();
    
    @Override
    @Transactional
    public boolean generateAndSendOtp(String email, String deviceId, String ipAddress) {
        try {
            log.info("Generating OTP for email: {} with device: {}", email, deviceId);
            
            // Validate email format
            validateEmail(email);
            
            // Check rate limiting
            if (isRateLimitReached(email)) {
                log.warn("Rate limit reached for email: {}", email);
                throw new BusinessException("Too many OTP requests. Please try again later.");
            }
            
            // Clean up old OTPs for this email
            otpCodeRepository.deleteOtpsByEmail(email);
            
            // Generate 6-digit OTP
            String otpCode = generateSixDigitOtp();
            
            // Calculate expiration time
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpirationMinutes);
            
            // Create OTP entity
            OtpCode otp = OtpCode.builder()
                    .email(email.toLowerCase().trim())
                    .code(otpCode)
                    .expiresAt(expiresAt)
                    .deviceId(deviceId)
                    .ipAddress(ipAddress)
                    .build();
            
            // Save OTP
            otpCodeRepository.save(otp);
            
            // Publish event for asynchronous email sending
            eventPublisher.publishEvent(new OtpEmailSentEvent(this, email, otpCode, otpExpirationMinutes));
            
            log.info("OTP generated successfully for email: {} - Email will be sent asynchronously", email);
            return true;
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error generating OTP for email: {} - {}", email, e.getMessage(), e);
            throw new BusinessException("Failed to generate OTP: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean validateOtp(String email, String code, String deviceId) {
        try {
            log.info("Validating OTP for email: {} with device: {}", email, deviceId);
            
            // Find valid OTP
            Optional<OtpCode> otpOpt = otpCodeRepository.findValidOtpByEmailAndCode(
                    email.toLowerCase().trim(), code, LocalDateTime.now());
            
            if (otpOpt.isEmpty()) {
                log.warn("Invalid OTP for email: {}", email);
                return false;
            }
            
            OtpCode otp = otpOpt.get();
            
            // Increment attempt count
            otp.incrementAttemptCount();
            
            // Check if max attempts reached
            if (otp.isMaxAttemptsReached()) {
                log.warn("Max attempts reached for OTP: {}", email);
                otpCodeRepository.save(otp);
                return false;
            }
            
            // Mark as used if valid
            if (otp.isValid()) {
                otp.markAsUsed();
                otpCodeRepository.save(otp);
                log.info("OTP validated successfully for email: {}", email);
                return true;
            }
            
            otpCodeRepository.save(otp);
            return false;
            
        } catch (Exception e) {
            log.error("Error validating OTP for email: {} - {}", email, e.getMessage(), e);
            throw new BusinessException("Failed to validate OTP: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public int cleanupExpiredOtps() {
        try {
            int deletedCount = otpCodeRepository.deleteExpiredOtps(LocalDateTime.now());
            log.info("Cleaned up {} expired OTP codes", deletedCount);
            return deletedCount;
        } catch (Exception e) {
            log.error("Error cleaning up expired OTPs: {}", e.getMessage(), e);
            return 0;
        }
    }
    
    @Override
    public boolean isRateLimitReached(String email) {
        try {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            long requestCount = otpCodeRepository.countOtpRequestsInLastHour(email.toLowerCase().trim(), oneHourAgo);
            
            boolean rateLimited = requestCount >= rateLimitPerHour;
            if (rateLimited) {
                log.warn("Rate limit reached for email: {} - {} requests in last hour", email, requestCount);
            }
            
            return rateLimited;
        } catch (Exception e) {
            log.error("Error checking rate limit for email: {} - {}", email, e.getMessage());
            return false; // Allow request if check fails
        }
    }
    
    @Override
    public int getRemainingAttempts(String email, String code) {
        try {
            Optional<OtpCode> otpOpt = otpCodeRepository.findValidOtpByEmailAndCode(
                    email.toLowerCase().trim(), code, LocalDateTime.now());
            
            if (otpOpt.isEmpty()) {
                return -1;
            }
            
            OtpCode otp = otpOpt.get();
            return otp.getMaxAttempts() - otp.getAttemptCount();
        } catch (Exception e) {
            log.error("Error getting remaining attempts for email: {} - {}", email, e.getMessage());
            return -1;
        }
    }
    
    /**
     * Generate 6-digit OTP code
     */
    private String generateSixDigitOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }
    
    /**
     * Validate email format
     */
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException("Email is required");
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$")) {
            throw new BusinessException("Invalid email format");
        }
    }
}
