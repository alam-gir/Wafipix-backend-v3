package com.wafipix.wafipix.common.security.entity;

import com.wafipix.wafipix.common.entity.BaseEntity;
import com.wafipix.wafipix.modules.user.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String token;

    @Column(nullable = false, name = "device_id")
    private String deviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
