package com.wafipix.wafipix.common.security.service;

import com.wafipix.wafipix.common.security.enums.TOKEN_TYPE;
import com.wafipix.wafipix.modules.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Map;

public interface JWTService {
    Boolean validateToken(String token);
    String getUsernameFromToken(String token);
    public Map<TOKEN_TYPE, String> refreshToken(User user, String refreshToken, String deviceId);
    public Map<TOKEN_TYPE, String> generateTokens(User user, String deviceId);
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String generateToken(String subject, String role, Long expires);
    Long getAccessTokenAge();
    Long getRefreshTokenAge();
    public void handleException(HttpServletRequest request, HttpServletResponse response, String message, HttpStatus status) throws IOException;
}
