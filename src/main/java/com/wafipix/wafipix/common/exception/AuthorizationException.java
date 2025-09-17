package com.wafipix.wafipix.common.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends BusinessException {
    
    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
    
    public AuthorizationException(String message, Throwable cause) {
        super(message, HttpStatus.FORBIDDEN, cause);
    }
}
