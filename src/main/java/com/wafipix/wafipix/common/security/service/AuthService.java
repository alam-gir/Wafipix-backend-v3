package com.wafipix.wafipix.common.security.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> refreshToken(HttpServletResponse response, String refreshToken, String deviceId);
    ResponseEntity<?> logout(String deviceId);
}
