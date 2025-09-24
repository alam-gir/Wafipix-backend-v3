package com.wafipix.wafipix.common.security.entity;

import com.wafipix.wafipix.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * OTP Code entity for admin/employee authentication
 * Stores 6-digit OTP codes with expiration and attempt tracking
 */
@Entity
@Table(name = "otp_codes", 
       indexes = {
           @Index(name = "idx_otp_email", columnList = "email"),
           @Index(name = "idx_otp_expires_at", columnList = "expiresAt"),
           @Index(name = "idx_otp_created_at", columnList = "createdAt")
       })
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpCode extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false, length = 6)
    private String code;
    
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    
    @Builder.Default
    @Column(nullable = false)
    private Integer attemptCount = 0;
    
    @Builder.Default
    @Column(nullable = false)
    private Integer maxAttempts = 3;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean isUsed = false;
    
    @Column(length = 100)
    private String deviceId;
    
    @Column(length = 50)
    private String ipAddress;
    
    // Helper methods
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isMaxAttemptsReached() {
        return attemptCount >= maxAttempts;
    }
    
    public boolean isValid() {
        return !isExpired() && !isUsed && !isMaxAttemptsReached();
    }
    
    public void incrementAttemptCount() {
        this.attemptCount++;
    }
    
    public void markAsUsed() {
        this.isUsed = true;
    }
}
