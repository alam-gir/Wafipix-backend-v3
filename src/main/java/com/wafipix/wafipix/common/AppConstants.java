package com.wafipix.wafipix.common;

import java.util.List;

public class AppConstants {
    public static final String OAUTH_REDIRECT_URI = "OAUTH_REDIRECT_URI";
    public static final String DEVICE_ID = "DEVICE_ID";
    
    // Public endpoints that don't require authentication
    public static final List<String> PUBLIC_ENDPOINTS = List.of(
        "/demo",
        "/v3/auth/**",
        "/v3/oauth2/**",
        "/v3/public/**",
        // Browser and static resources
        "/favicon.ico",
        "/robots.txt",
        "/sitemap.xml",
        "/.well-known/**",
        // Health check endpoints (common in production)
        "/health",
        "/actuator/health",
        "/actuator/info"
    );
}
