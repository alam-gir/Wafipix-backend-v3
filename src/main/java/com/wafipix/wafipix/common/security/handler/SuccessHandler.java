package com.wafipix.wafipix.common.security.handler;

import com.wafipix.wafipix.common.exception.AuthenticationException;
import com.wafipix.wafipix.common.exception.BusinessException;
import com.wafipix.wafipix.common.security.enums.TOKEN_TYPE;
import com.wafipix.wafipix.common.security.service.CookieService;
import com.wafipix.wafipix.common.security.service.impl.JWTServiceImpl;
import com.wafipix.wafipix.modules.user.entity.User;
import com.wafipix.wafipix.modules.user.mapper.UserMapper;
import com.wafipix.wafipix.modules.user.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static com.wafipix.wafipix.common.AppConstants.DEVICE_ID;
import static com.wafipix.wafipix.common.AppConstants.OAUTH_REDIRECT_URI;


@Component
@RequiredArgsConstructor
@Slf4j
public class SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTServiceImpl jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CookieService cookieService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            log.info("Processing OAuth2 authentication success");
            
            // Extract session attributes with validation
            String redirectUri = extractSessionAttribute(request, OAUTH_REDIRECT_URI, "Redirect URI");
            String deviceId = extractSessionAttribute(request, DEVICE_ID, "Device ID");

            // Validate authentication type
            OAuth2AuthenticationToken token = validateAuthenticationType(authentication);
            OAuth2User oauth2User = token.getPrincipal();

            // Extract and validate email
            String email = extractAndValidateEmail(oauth2User);

            // Find or create user
            User user = findOrCreateUser(oauth2User, email);

            // Generate tokens and set cookies
            generateTokensAndSetCookies(user, deviceId, response);

            // Redirect user to the original URL
            log.info("Successfully authenticated user: {} - redirecting to: {}", email, redirectUri);
            response.sendRedirect(redirectUri);
            
        } catch (AuthenticationException e) {
            log.error("Authentication error during OAuth2 success: {}", e.getMessage());
            response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        } catch (BusinessException e) {
            log.error("Business error during OAuth2 success: {}", e.getMessage());
            response.sendError(e.getStatus().value(), e.getMessage());
        } catch (Exception ex) {
            log.error("Unexpected error during OAuth2 authentication success: {}", ex.getMessage(), ex);
            response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Authentication failed");
        }
    }

    /**
     * Extract session attribute with validation
     */
    private String extractSessionAttribute(HttpServletRequest request, String attributeName, String attributeDescription) {
        Object attribute = request.getSession().getAttribute(attributeName);
        if (attribute == null) {
            throw new BusinessException(attributeDescription + " not found in session");
        }
        return attribute.toString();
    }

    /**
     * Validate authentication type and return OAuth2 token
     */
    private OAuth2AuthenticationToken validateAuthenticationType(Authentication authentication) {
        if (!(authentication instanceof OAuth2AuthenticationToken token)) {
            throw new AuthenticationException("Unexpected authentication type: " + authentication.getClass().getName());
        }
        return token;
    }

    /**
     * Extract and validate email from OAuth2 user
     */
    private String extractAndValidateEmail(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        if (email == null || email.isBlank()) {
            throw new AuthenticationException("Email not provided by OAuth provider");
        }
        return email;
    }

    /**
     * Find existing user or create new one from OAuth2 data
     */
    private User findOrCreateUser(OAuth2User oauth2User, String email) {
        // Note: This method needs to be adapted based on actual UserRepository methods
        // For now, we'll use a placeholder that maintains the original functionality
        return userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    log.info("Creating new user for email: {}", email);
                    String name = oauth2User.getAttribute("name");
                    String picture = oauth2User.getAttribute("picture");
                    User newUser = userMapper.toEntity(name, email, picture, "ADMIN");
                    return userRepository.save(newUser);
                });
    }

    /**
     * Generate JWT tokens and set them as cookies
     */
    private void generateTokensAndSetCookies(User user, String deviceId, HttpServletResponse response) {
        Map<TOKEN_TYPE, String> tokens = jwtService.generateTokens(user, deviceId);
        
        // Set access token cookie
        response.addCookie(cookieService.create("at", tokens.get(TOKEN_TYPE.ACCESS_TOKEN)));
        
        // Set refresh token cookie
        response.addCookie(cookieService.create("rt", tokens.get(TOKEN_TYPE.REFRESH_TOKEN)));
        
        log.debug("Generated tokens for user: {} with device: {}", user.getEmail(), deviceId);
    }

}