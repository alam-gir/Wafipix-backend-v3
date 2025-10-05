package com.wafipix.wafipix.common.security.service.impl;

import com.wafipix.wafipix.common.exception.AuthenticationException;
import com.wafipix.wafipix.common.exception.AuthorizationException;
import com.wafipix.wafipix.common.exception.BusinessException;
import com.wafipix.wafipix.common.security.dto.AdminProfileResponse;
import com.wafipix.wafipix.common.security.enums.TOKEN_TYPE;
import com.wafipix.wafipix.common.security.mapper.SecurityMapper;
import com.wafipix.wafipix.common.security.repository.RefreshTokenRepository;
import com.wafipix.wafipix.common.security.service.AuthService;
import com.wafipix.wafipix.common.security.service.CookieService;
import com.wafipix.wafipix.common.security.service.JWTService;
import com.wafipix.wafipix.modules.user.entity.User;
import com.wafipix.wafipix.modules.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final CookieService cookieService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecurityMapper securityMapper;

    @Override
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response, String deviceId) {
        try {
            log.info("Processing refresh token request for device: {}", deviceId);
            
            // Extract refresh token from cookies
            String refreshToken = extractRefreshTokenFromCookies(request);
            
            // Validate input parameters
            validateRefreshTokenRequest(refreshToken, deviceId);

            // Validate the refresh token
            if (!jwtService.validateToken(refreshToken)) {
                log.warn("Invalid refresh token for device: {}", deviceId);
                throw new AuthorizationException("Refresh token expired or invalid. Please login again.");
            }

            // Extract username from token
            String username = jwtService.getUsernameFromToken(refreshToken);
            log.debug("Extracted username from refresh token: {}", username);

            // Find user by email
            Optional<User> user = userRepository.findByEmailIgnoreCase(username);
            if (user.isEmpty()) {
                log.warn("User not found for refresh token: {}", username);
                throw new AuthenticationException("User not found");
            }

            // Generate new tokens
            Map<TOKEN_TYPE, String> tokens = jwtService.refreshToken(user.get(), refreshToken, deviceId);
            log.debug("Generated new tokens for user: {}", username);

            // Set cookies
            response.addCookie(cookieService.create("at", tokens.get(TOKEN_TYPE.ACCESS_TOKEN)));
            response.addCookie(cookieService.create("rt", tokens.get(TOKEN_TYPE.REFRESH_TOKEN)));

            // Create profile response (like getProfile)
            AdminProfileResponse profileResponse = securityMapper.toProfileResponse(user.get());

            log.info("Successfully refreshed tokens for user: {} with device: {}", username, deviceId);
            return new ResponseEntity<>(profileResponse, HttpStatus.OK);

        } catch (AuthenticationException e) {
            log.error("Authentication error during token refresh: {}", e.getMessage());
            throw e;
        } catch (AuthorizationException e) {
            log.error("Authorization error during token refresh: {}", e.getMessage());
            throw e;
        } catch (BusinessException e) {
            log.error("Business error during token refresh: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during token refresh: {}", e.getMessage(), e);
            throw new BusinessException("Failed to refresh tokens: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> logout(String deviceId) {
        try {
            log.info("Processing logout request for device: {}", deviceId);
            
            // Validate device ID
            if (deviceId == null || deviceId.trim().isEmpty()) {
                throw new BusinessException("Device ID is required for logout");
            }

            // Delete refresh tokens for the device
            refreshTokenRepository.deleteByDeviceId(deviceId);
            log.debug("Deleted refresh tokens for device: {}", deviceId);

            // Create expired cookies to clear client-side tokens
            ResponseCookie atCookie = ResponseCookie.from("at", "")
                    .httpOnly(true)
                    .secure(true) // Set to true if using HTTPS
                    .path("/")
                    .sameSite("Lax") // or "None" if cross-site
                    .maxAge(0)
                    .build();

            ResponseCookie rtCookie = ResponseCookie.from("rt", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("Lax")
                    .maxAge(0)
                    .build();

            log.info("Successfully logged out device: {}", deviceId);
            return ResponseEntity
                    .noContent()
                    .header(HttpHeaders.SET_COOKIE, atCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, rtCookie.toString())
                    .build();
                    
        } catch (BusinessException e) {
            log.error("Business error during logout: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during logout: {}", e.getMessage(), e);
            throw new BusinessException("Failed to logout: " + e.getMessage());
        }
    }

    /**
     * Extracts refresh token from HTTP cookies
     * 
     * @param request HTTP request containing cookies
     * @return Refresh token string
     * @throws BusinessException if refresh token cookie is not found
     */
    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rt".equals(cookie.getName())) {
                    String refreshToken = cookie.getValue();
                    if (refreshToken != null && !refreshToken.trim().isEmpty()) {
                        log.debug("Successfully extracted refresh token from cookies");
                        return refreshToken;
                    }
                }
            }
        }
        
        log.warn("Refresh token cookie 'rt' not found in request");
        throw new AuthorizationException("Refresh token cookie not found. Please login again.");
    }

    /**
     * Validates refresh token request parameters
     * 
     * @param refreshToken The refresh token string
     * @param deviceId The device ID
     * @throws BusinessException if validation fails
     */
    private void validateRefreshTokenRequest(String refreshToken, String deviceId) {
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            throw new BusinessException("Refresh token is required");
        }
        
        if (deviceId == null || deviceId.trim().isEmpty()) {
            throw new BusinessException("Device ID is required");
        }
        
        log.debug("Refresh token request validation passed");
    }
}
