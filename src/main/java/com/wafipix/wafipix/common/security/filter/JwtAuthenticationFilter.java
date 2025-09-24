package com.wafipix.wafipix.common.security.filter;

import com.wafipix.wafipix.common.AppConstants;
import com.wafipix.wafipix.common.exception.AuthenticationException;
import com.wafipix.wafipix.common.exception.AuthorizationException;
import com.wafipix.wafipix.common.exception.BusinessException;
import com.wafipix.wafipix.common.security.service.JWTService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain) throws ServletException, IOException {

        // Double-check: Skip processing for public endpoints
        if (shouldNotFilter(request)) {
            log.debug("JWT Filter - Skipping public endpoint: {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }


        try {
            final String authHeader = request.getHeader("Authorization");
            String token = null;

            // Extract token from Authorization header
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }

            // Extract token from cookies if not found in header
            if (token == null && request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("at".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            // If no access token found, check if refresh token exists to determine proper status code
            if (token == null) {
                boolean hasRefreshToken = hasRefreshTokenCookie(request);
                if (hasRefreshToken) {
                    // User has refresh token but no access token - token expired (401)
                    log.warn("No access token found but refresh token exists - token expired");
                    jwtService.handleException(
                            request,
                            response,
                            "Access token expired. Please refresh your token.",
                            HttpStatus.UNAUTHORIZED
                    );
                    return;
                } else {
                    // No tokens at all - user logged out (403)
                    log.warn("No authentication tokens found - user logged out");
                    jwtService.handleException(
                            request,
                            response,
                            "Authentication required. Please login.",
                            HttpStatus.FORBIDDEN
                    );
                    return;
                }
            }

            // Process token
            String username = jwtService.getUsernameFromToken(token);
            Authentication contextHolder = SecurityContextHolder.getContext().getAuthentication();

            // Only authenticate if username is valid and no existing authentication
            if (username != null && contextHolder == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate token
                if (jwtService.validateToken(token)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    // Token invalid - check if refresh token exists
                    boolean hasRefreshToken = hasRefreshTokenCookie(request);
                    if (hasRefreshToken) {
                        log.warn("Invalid access token but refresh token exists - token expired");
                        jwtService.handleException(
                                request,
                                response,
                                "Access token expired. Please refresh your token.",
                                HttpStatus.UNAUTHORIZED
                        );
                        return;
                    } else {
                        log.warn("Invalid access token and no refresh token - user logged out");
                        jwtService.handleException(
                                request,
                                response,
                                "Authentication required. Please login.",
                                HttpStatus.FORBIDDEN
                        );
                        return;
                    }
                }
            }
            
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.error("JWT token expired: {}", e.getMessage());
            // Check if refresh token exists to determine proper status code
            boolean hasRefreshToken = hasRefreshTokenCookie(request);
            HttpStatus status = hasRefreshToken ? HttpStatus.UNAUTHORIZED : HttpStatus.FORBIDDEN;
            String message = hasRefreshToken ? 
                "Access token expired. Please refresh your token." : 
                "Authentication required. Please login.";
            
            jwtService.handleException(request, response, message, status);
        } catch (MalformedJwtException | JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            jwtService.handleException(
                    request,
                    response,
                    "Invalid token format",
                    HttpStatus.FORBIDDEN
            );
        } catch (UsernameNotFoundException e) {
            log.error("User not found in JWT filter: {}", e.getMessage());
            jwtService.handleException(
                    request,
                    response,
                    "User not found",
                    HttpStatus.NOT_FOUND
            );
        } catch (AuthenticationException e) {
            log.error("Authentication error in JWT filter: {}", e.getMessage());
            jwtService.handleException(
                    request,
                    response,
                    e.getMessage(),
                    HttpStatus.UNAUTHORIZED
            );
        } catch (AuthorizationException e) {
            log.error("Authorization error in JWT filter: {}", e.getMessage());
            jwtService.handleException(
                    request,
                    response,
                    e.getMessage(),
                    HttpStatus.FORBIDDEN
            );
        } catch (BusinessException e) {
            log.error("Business error in JWT filter: {}", e.getMessage());
            jwtService.handleException(
                    request,
                    response,
                    e.getMessage(),
                    e.getStatus()
            );
        } catch (Exception e) {
            log.error("Unexpected error in JWT filter: {}", e.getMessage(), e);
            jwtService.handleException(
                    request,
                    response,
                    "Internal server error",
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

    }

    /**
     * Check if request has refresh token cookie
     * 
     * @param request HTTP request
     * @return true if refresh token cookie exists, false otherwise
     */
    private boolean hasRefreshTokenCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("rt".equals(cookie.getName()) && 
                    cookie.getValue() != null && 
                    !cookie.getValue().trim().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        
        // Skip JWT filter for public endpoints that don't require authentication
        return AppConstants.PUBLIC_ENDPOINTS.stream()
                .anyMatch(endpoint -> {
                    if (endpoint.endsWith("/**")) {
                        // Handle wildcard patterns like "/v3/auth/**"
                        String basePath = endpoint.substring(0, endpoint.length() - 3);
                        return requestURI.startsWith(basePath);
                    } else {
                        // Handle exact matches like "/demo"
                        return requestURI.equals(endpoint);
                    }
                });
    }

}