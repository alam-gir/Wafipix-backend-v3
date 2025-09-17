package com.wafipix.wafipix.modules.user.repository;

import com.wafipix.wafipix.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Simple User Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    /**
     * Find user by email (case insensitive)
     */
    Optional<User> findByEmailIgnoreCase(String email);
}
