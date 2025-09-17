package com.wafipix.wafipix.common.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends BusinessException {
    
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, HttpStatus.UNAUTHORIZED, cause);
    }
}
