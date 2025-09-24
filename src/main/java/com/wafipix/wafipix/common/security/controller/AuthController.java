package com.wafipix.wafipix.common.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wafipix.wafipix.common.security.service.AuthService;

@RestController
@RequestMapping("/v3/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/refresh-token/{deviceId}")
    public ResponseEntity<?> refreshToken(
            HttpServletRequest request, 
            HttpServletResponse response, 
            @PathVariable String deviceId) {
        try {
            log.info("Received refresh token request for device: {}", deviceId);
            return authService.refreshToken(request, response, deviceId);
        } catch (Exception e) {
            log.error("Error in refresh token endpoint: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/logout/{deviceId}")
    public ResponseEntity<?> logout(HttpServletResponse response, @PathVariable String deviceId) {
        try {
            log.info("Received logout request for device: {}", deviceId);
            return authService.logout(deviceId);
        } catch (Exception e) {
            log.error("Error in logout endpoint: {}", e.getMessage(), e);
            throw e;
        }
    }
}
