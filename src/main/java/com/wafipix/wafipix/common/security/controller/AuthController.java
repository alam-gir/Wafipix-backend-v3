package com.wafipix.wafipix.common.security.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.wafipix.wafipix.common.security.service.AuthService;

@RestController
@RequestMapping("/v3/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletResponse response, @RequestPart String refresh_token, @RequestPart String device_id) {
        return authService.refreshToken(response, refresh_token, device_id);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, @RequestPart String device_id) {
        return authService.logout(device_id);
    }
}
