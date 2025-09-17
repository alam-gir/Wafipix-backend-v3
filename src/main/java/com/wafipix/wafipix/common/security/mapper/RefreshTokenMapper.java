package com.wafipix.wafipix.common.security.mapper;

import com.wafipix.wafipix.common.security.dto.response.RefreshTokenResponseDTO;
import com.wafipix.wafipix.common.security.entity.RefreshToken;
import com.wafipix.wafipix.modules.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenMapper {
    public RefreshToken toEntity(String refreshToken, User user, String deviceId) {
        return RefreshToken.builder()
                .token(refreshToken)
                .user(user)
                .deviceId(deviceId)
                .build();
    }

    public RefreshTokenResponseDTO toRefreshTokenResponseDTO(String refreshToken, String accessToken) {
        return new RefreshTokenResponseDTO(refreshToken, accessToken);
    }
}
