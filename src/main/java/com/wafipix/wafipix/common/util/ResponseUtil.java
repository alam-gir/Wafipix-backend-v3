package com.wafipix.wafipix.common.util;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.common.dto.FieldError;
import com.wafipix.wafipix.common.dto.PaginationInfo;

import java.util.List;
import java.util.stream.Collectors;

public class ResponseUtil {
    
    // Success responses
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(ApiResponse.success(data));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message) {
        return ResponseEntity.ok(ApiResponse.success(data, message));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> success(T data, String message, PaginationInfo pagination) {
        return ResponseEntity.ok(ApiResponse.success(data, message, pagination));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> created(T data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(data, message));
    }
    
    // Paginated responses
    public static <T> ResponseEntity<ApiResponse<List<T>>> paginated(Page<T> page, String message) {
        PaginationInfo pagination = PaginationInfo.of(
                page.getNumber() + 1, // Spring Data uses 0-based indexing
                page.getSize(),
                page.getTotalElements()
        );
        
        return ResponseEntity.ok(ApiResponse.success(page.getContent(), message, pagination));
    }
    
    // Error responses
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(message, status.value()));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> error(String message, HttpStatus status, List<FieldError> errors) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(message, status.value(), errors));
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return error(message, HttpStatus.BAD_REQUEST);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message, List<FieldError> errors) {
        return error(message, HttpStatus.BAD_REQUEST, errors);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return error(message, HttpStatus.NOT_FOUND);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return error(message, HttpStatus.UNAUTHORIZED);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return error(message, HttpStatus.FORBIDDEN);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> internalError(String message) {
        return error(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // Validation error handling
    public static List<FieldError> extractFieldErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> FieldError.of(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .collect(Collectors.toList());
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> validationError(BindingResult bindingResult) {
        List<FieldError> errors = extractFieldErrors(bindingResult);
        return badRequest("Validation failed", errors);
    }
}
