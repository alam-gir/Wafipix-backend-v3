package com.wafipix.wafipix.common.security.service.impl;

import com.wafipix.wafipix.common.security.entity.RefreshToken;
import com.wafipix.wafipix.common.security.mapper.RefreshTokenMapper;
import com.wafipix.wafipix.common.security.repository.RefreshTokenRepository;
import com.wafipix.wafipix.common.security.service.RefreshTokenService;
import com.wafipix.wafipix.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMapper refreshTokenMapper;

    @Override
    public RefreshToken save(User user, String refreshToken, String deviceId) {
        // check user has old tokens with same device id
        // if have, then remove old token for same device id
        Optional<RefreshToken> oldToken = refreshTokenRepository.findByUserAndDeviceId(user, deviceId);

        if (oldToken.isPresent()) {
            oldToken.get().setToken(refreshToken);
            return refreshTokenRepository.save(oldToken.get());
        } else return refreshTokenRepository.save(refreshTokenMapper.toEntity(refreshToken, user, deviceId));

    }
}
