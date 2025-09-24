package com.wafipix.wafipix.common.security.repository;

import com.wafipix.wafipix.common.security.entity.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for OTP Code entity
 * Handles OTP code storage and retrieval operations
 */
@Repository
public interface OtpCodeRepository extends JpaRepository<OtpCode, java.util.UUID> {
    
    /**
     * Find valid OTP code by email and code
     * @param email User email
     * @param code OTP code
     * @return Optional OTP code if found and valid
     */
    @Query("SELECT o FROM OtpCode o WHERE o.email = :email AND o.code = :code AND o.isUsed = false AND o.expiresAt > :now AND o.attemptCount < o.maxAttempts")
    Optional<OtpCode> findValidOtpByEmailAndCode(@Param("email") String email, @Param("code") String code, @Param("now") LocalDateTime now);
    
    /**
     * Find latest OTP code for email (for rate limiting)
     * @param email User email
     * @return Optional latest OTP code
     */
    @Query("SELECT o FROM OtpCode o WHERE o.email = :email ORDER BY o.createdAt DESC")
    Optional<OtpCode> findLatestOtpByEmail(@Param("email") String email);
    
    /**
     * Count OTP requests for email in the last hour
     * @param email User email
     * @param oneHourAgo Time one hour ago
     * @return Count of OTP requests
     */
    @Query("SELECT COUNT(o) FROM OtpCode o WHERE o.email = :email AND o.createdAt > :oneHourAgo")
    long countOtpRequestsInLastHour(@Param("email") String email, @Param("oneHourAgo") LocalDateTime oneHourAgo);
    
    /**
     * Delete expired OTP codes
     * @param now Current time
     * @return Number of deleted records
     */
    @Modifying
    @Query("DELETE FROM OtpCode o WHERE o.expiresAt < :now")
    int deleteExpiredOtps(@Param("now") LocalDateTime now);
    
    /**
     * Delete OTP codes for specific email
     * @param email User email
     * @return Number of deleted records
     */
    @Modifying
    @Query("DELETE FROM OtpCode o WHERE o.email = :email")
    int deleteOtpsByEmail(@Param("email") String email);
    
    /**
     * Find all OTP codes for email (for cleanup)
     * @param email User email
     * @return List of OTP codes
     */
    List<OtpCode> findByEmailOrderByCreatedAtDesc(String email);
}
