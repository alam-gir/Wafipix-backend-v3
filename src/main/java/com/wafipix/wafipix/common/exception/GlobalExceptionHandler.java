package com.wafipix.wafipix.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.common.dto.FieldError;
import com.wafipix.wafipix.common.util.ResponseUtil;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // Validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.warn("Validation error: {}", ex.getMessage());
        
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> errors = ResponseUtil.extractFieldErrors(bindingResult);
        
        return ResponseUtil.badRequest("Validation failed", errors);
    }
    
    // Custom business exceptions
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        log.warn("Business exception: {}", ex.getMessage());
        
        return ResponseUtil.error(ex.getMessage(), ex.getStatus());
    }
    
    // Resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        log.warn("Resource not found: {}", ex.getMessage());
        
        return ResponseUtil.notFound(ex.getMessage());
    }
    
    // Authentication exceptions
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(
            AuthenticationException ex, WebRequest request) {
        
        log.warn("Authentication error: {}", ex.getMessage());
        
        return ResponseUtil.unauthorized(ex.getMessage());
    }
    
    // Authorization exceptions
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthorizationException(
            AuthorizationException ex, WebRequest request) {
        
        log.warn("Authorization error: {}", ex.getMessage());
        
        return ResponseUtil.forbidden(ex.getMessage());
    }
    
    // Generic exception handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        log.error("Unexpected error occurred", ex);
        
        return ResponseUtil.internalError("An unexpected error occurred");
    }
}
