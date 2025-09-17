package com.wafipix.wafipix.common.security.repository;

import com.wafipix.wafipix.common.security.entity.RefreshToken;
import com.wafipix.wafipix.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByUserAndDeviceId(User user,String deviceId);
    Optional<RefreshToken> findByUserAndTokenAndDeviceId(User user, String token, String deviceId);

    void deleteByDeviceId(String deviceId);
}
