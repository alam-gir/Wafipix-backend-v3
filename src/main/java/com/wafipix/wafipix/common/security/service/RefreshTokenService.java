package com.wafipix.wafipix.common.security.service;

import com.wafipix.wafipix.common.security.entity.RefreshToken;
import com.wafipix.wafipix.modules.user.entity.User;

public interface RefreshTokenService {
    RefreshToken save(User user, String refreshToken, String deviceId);
}
