package com.wafipix.wafipix.common.security.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.common.exception.AuthenticationException;
import com.wafipix.wafipix.common.exception.AuthorizationException;
import com.wafipix.wafipix.common.exception.BusinessException;
import com.wafipix.wafipix.common.security.entity.RefreshToken;
import com.wafipix.wafipix.common.security.enums.TOKEN_TYPE;
import com.wafipix.wafipix.common.security.repository.RefreshTokenRepository;
import com.wafipix.wafipix.common.security.service.JWTService;
import com.wafipix.wafipix.common.security.service.RefreshTokenService;
import com.wafipix.wafipix.modules.user.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTServiceImpl implements JWTService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.SECRET}")
    private String SECRET;
    @Value("${jwt.ACCESS_TOKEN_EXPIRATION}")
    private Long ACCESS_TOKEN_EXPIRATION;
    @Value("${jwt.REFRESH_TOKEN_EXPIRATION}")
    private Long REFRESH_TOKEN_EXPIRATION;
    private final RefreshTokenService refreshTokenService;


    @Override
    public Boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            // Check if token is not expired
            boolean isValid = !claims.getExpiration().before(new Date());
            
            if (isValid) {
                log.debug("Token validation successful for subject: {}", claims.getSubject());
            } else {
                log.warn("Token validation failed - token expired");
            }
            
            return isValid;
        } catch (ExpiredJwtException e) {
            log.warn("Token validation failed - token expired: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            log.warn("Token validation failed - malformed token: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Token validation failed - unexpected error: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        try {
            String username = getClaimFromToken(token, Claims::getSubject);
            log.debug("Extracted username from token: {}", username);
            return username;
        } catch (ExpiredJwtException e) {
            log.warn("Failed to extract username - token expired: {}", e.getMessage());
            throw new AuthenticationException("Token expired");
        } catch (MalformedJwtException e) {
            log.warn("Failed to extract username - malformed token: {}", e.getMessage());
            throw new AuthorizationException("Invalid token format");
        } catch (Exception e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            throw new BusinessException("Failed to extract username from token");
        }
    }

    @Override
    public Map<TOKEN_TYPE, String> refreshToken(User user, String refreshToken, String deviceId) {
        try {
            log.info("Refreshing tokens for user: {} with device: {}", user.getEmail(), deviceId);
            
            Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserAndTokenAndDeviceId(user, refreshToken, deviceId);

            if (existingToken.isPresent()) {
                log.debug("Valid refresh token found, generating new tokens");
                return generateTokens(user, deviceId);
            } else {
                log.warn("Invalid refresh token for user: {} with device: {}", user.getEmail(), deviceId);
                throw new AuthorizationException("Invalid refresh token");
            }
        } catch (AuthorizationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error refreshing tokens for user: {} - {}", user.getEmail(), e.getMessage(), e);
            throw new BusinessException("Failed to refresh tokens: " + e.getMessage());
        }
    }

    @Override
    public Map<TOKEN_TYPE, String> generateTokens(User user, String deviceId) {
        try {
            log.info("Generating tokens for user: {} with device: {}", user.getEmail(), deviceId);
            
            String accessToken = generateAccessToken(user);
            String refreshToken = generateRefreshToken(user);

            Map<TOKEN_TYPE, String> tokens = new HashMap<>();
            tokens.put(TOKEN_TYPE.ACCESS_TOKEN, accessToken);
            tokens.put(TOKEN_TYPE.REFRESH_TOKEN, refreshToken);

            // Save refresh token to database
            refreshTokenService.save(user, refreshToken, deviceId);
            
            log.debug("Successfully generated tokens for user: {}", user.getEmail());
            return tokens;
        } catch (Exception e) {
            log.error("Error generating tokens for user: {} - {}", user.getEmail(), e.getMessage(), e);
            throw new BusinessException("Failed to generate tokens: " + e.getMessage());
        }
    }

    @Override
    public String generateAccessToken(User user) {
        try {
            log.debug("Generating access token for user: {}", user.getEmail());
            return generateToken(user.getEmail(), user.getRole().name(), ACCESS_TOKEN_EXPIRATION);
        } catch (Exception e) {
            log.error("Error generating access token for user: {} - {}", user.getEmail(), e.getMessage());
            throw new BusinessException("Failed to generate access token: " + e.getMessage());
        }
    }

    @Override
    public String generateRefreshToken(User user) {
        try {
            log.debug("Generating refresh token for user: {}", user.getEmail());
            return generateToken(user.getEmail(), user.getRole().name(), REFRESH_TOKEN_EXPIRATION);
        } catch (Exception e) {
            log.error("Error generating refresh token for user: {} - {}", user.getEmail(), e.getMessage());
            throw new BusinessException("Failed to generate refresh token: " + e.getMessage());
        }
    }

    @Override
    public String generateToken(String subject, String role, Long expires) {
        try {
            log.debug("Generating JWT token for subject: {} with role: {}", subject, role);
            
            return Jwts.builder()
                    .subject(subject)
                    .claim("roles", role)
                    .expiration(Date.from(Instant.now().plus(Duration.ofMillis(expires))))
                    .issuedAt(Date.from(Instant.now()))
                    .signWith(getKey())
                    .compact();
        } catch (Exception e) {
            log.error("Error generating JWT token for subject: {} - {}", subject, e.getMessage());
            throw new BusinessException("Failed to generate JWT token: " + e.getMessage());
        }
    }

    @Override
    public Long getAccessTokenAge() {
        return ACCESS_TOKEN_EXPIRATION;
    }

    @Override
    public Long getRefreshTokenAge() {
        return REFRESH_TOKEN_EXPIRATION;
    }

    @Override
    public void handleException(HttpServletRequest request, HttpServletResponse response, String message, HttpStatus status) throws IOException {
        try {
            log.warn("Handling JWT exception: {} - Status: {}", message, status);
            
            // Create standardized error response using our ApiResponse
            ApiResponse<Object> errorResponse = ApiResponse.<Object>builder()
                    .success(false)
                    .message(message)
                    .statusCode(status.value())
                    .timestamp(java.time.LocalDateTime.now())
                    .path(request.getRequestURI())
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);

            response.setContentType("application/json");
            response.setStatus(status.value());
            response.getWriter().write(jsonResponse);
            
        } catch (Exception e) {
            log.error("Error handling JWT exception: {}", e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("{\"success\":false,\"message\":\"Internal server error\"}");
        }
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            Claims claims = getAllClaimsFromToken(token);
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            log.warn("Token expired while extracting claims: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("Malformed token while extracting claims: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error extracting claims from token: {}", e.getMessage());
            throw new BusinessException("Failed to extract claims from token");
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getKey() {
        try {
            return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        } catch (Exception e) {
            log.error("Error creating JWT signing key: {}", e.getMessage());
            throw new BusinessException("Failed to create JWT signing key");
        }
    }
}
