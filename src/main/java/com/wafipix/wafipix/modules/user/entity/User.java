package com.wafipix.wafipix.modules.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.wafipix.wafipix.common.entity.Auditable;
import com.wafipix.wafipix.modules.user.enums.AuthProvider;
import com.wafipix.wafipix.modules.user.enums.UserRole;

import java.time.LocalDateTime;

/**
 * User entity for JWT service compatibility
 * This entity matches what the JWT service expects
 */
@Entity
@Table(name = "users", 
       uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends Auditable {
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false, length = 50)
    private String firstName;
    
    @Column(nullable = false, length = 50)
    private String lastName;
    
    @Column(length = 20)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;
    
    @Column(length = 255)
    private String password; // Only for CUSTOM auth provider
    
    @Column(length = 255)
    private String providerId; // OAuth2 provider user ID
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;
    
    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public boolean isCustomer() {
        return UserRole.CUSTOMER.equals(role);
    }
    
    public boolean isEmployee() {
        return role == UserRole.SUPPORT || role == UserRole.DESIGNER;
    }
    
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(role);
    }
    
    public boolean hasPassword() {
        return password != null && !password.trim().isEmpty();
    }
}
