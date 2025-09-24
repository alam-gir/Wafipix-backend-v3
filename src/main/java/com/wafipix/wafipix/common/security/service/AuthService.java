package com.wafipix.wafipix.common.security.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response, String deviceId);
    ResponseEntity<?> logout(String deviceId);
}
