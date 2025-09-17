package com.wafipix.wafipix.common.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wafipix.wafipix.common.exception.BusinessException;

import java.io.IOException;

import static com.wafipix.wafipix.common.AppConstants.DEVICE_ID;
import static com.wafipix.wafipix.common.AppConstants.OAUTH_REDIRECT_URI;

@RestController
@RequestMapping("/v3/oauth2")
public class OAuthController {

    @GetMapping(value = {"/google", "/google/"})
    public void google(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "redirect_uri", required = false) String redirectUri,
            @RequestParam(value = "device_id", required = false) String deviceId) throws IOException {
        
        try {
            // Validate parameters
            if (redirectUri == null || redirectUri.trim().isEmpty()) {
                throw new BusinessException("Redirect URI is required");
            }
            if (deviceId == null || deviceId.trim().isEmpty()) {
                throw new BusinessException("Device ID is required");
            }

            // Set to session
            request.getSession().setAttribute(OAUTH_REDIRECT_URI, redirectUri);
            request.getSession().setAttribute(DEVICE_ID, deviceId);

            // Redirect directly to Google OAuth
            response.sendRedirect("/oauth2/authorization/google");
            
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Failed to initiate OAuth flow: " + e.getMessage());
        }
    }
}
