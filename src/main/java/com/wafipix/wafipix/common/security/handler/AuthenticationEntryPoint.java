package com.wafipix.wafipix.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wafipix.wafipix.common.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        try {
            Object exception = request.getAttribute("exception");
            Object statusCode = request.getAttribute("statusCode");

            HttpStatus httpStatus;
            String errorMessage;

            if (exception != null && statusCode != null) {
                // Use the exception and status code from request attributes
                httpStatus = HttpStatus.valueOf(Integer.parseInt(statusCode.toString()));
                errorMessage = exception.toString();
                log.warn("Authentication failed for URI '{}': {} - {}", request.getRequestURI(), httpStatus, errorMessage);
            } else {
                // Fallback to default authentication error
                httpStatus = HttpStatus.UNAUTHORIZED;
                errorMessage = authException != null ? authException.getMessage() : "Authentication required";
                log.warn("Authentication failed for URI '{}' with default error: {}", request.getRequestURI(), errorMessage);
            }

            // Create standardized error response using our ResponseUtil
            ApiResponse<Object> errorResponse = ApiResponse.<Object>builder()
                    .success(false)
                    .message(errorMessage)
                    .statusCode(httpStatus.value())
                    .timestamp(java.time.LocalDateTime.now().toString())
                    .path(request.getRequestURI())
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(errorResponse);

            response.setContentType("application/json");
            response.setStatus(httpStatus.value());
            response.getWriter().write(jsonResponse);

        } catch (Exception e) {
            log.error("Error in AuthenticationEntryPoint: {}", e.getMessage(), e);
            
            // Fallback error response
            ApiResponse<Object> fallbackResponse = ApiResponse.<Object>builder()
                    .success(false)
                    .message("Authentication failed")
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .timestamp(java.time.LocalDateTime.now().toString())
                    .build();

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(fallbackResponse);

            response.setContentType("application/json");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(jsonResponse);
        }
    }
}
