package com.wafipix.wafipix.common.security.filter;

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

            // If no token found, continue to next filter
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
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
                    throw new AuthenticationException("Invalid or expired token");
                }
            }
            
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.error("JWT token expired: {}", e.getMessage());
            jwtService.handleException(
                    request,
                    response,
                    "Token expired",
                    HttpStatus.UNAUTHORIZED
            );
        } catch (MalformedJwtException | JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            jwtService.handleException(
                    request,
                    response,
                    "Invalid token",
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

    @Override
    public boolean shouldNotFilter(@NotNull HttpServletRequest request) {
        // Skip JWT filter for public endpoints that don't require authentication
        String requestURI = request.getRequestURI();
        
        // Skip for OAuth2 endpoints
        if (requestURI.startsWith("/v3/oauth2")) {
            return true;
        }
        
        // Skip for authentication endpoints
        if (requestURI.startsWith("/v3/auth")) {
            return true;
        }
        
        // Skip for public API endpoints (if any)
        if (requestURI.startsWith("/v3/public")) {
            return true;
        }
        
        return false;
    }

}