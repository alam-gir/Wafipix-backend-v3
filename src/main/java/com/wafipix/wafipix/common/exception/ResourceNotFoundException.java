package com.wafipix.wafipix.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
    
    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s with id %s not found", resource, id), HttpStatus.NOT_FOUND);
    }
    
    public ResourceNotFoundException(String resource, String field, String value) {
        super(String.format("%s with %s '%s' not found", resource, field, value), HttpStatus.NOT_FOUND);
    }
    
    public ResourceNotFoundException(String resource, java.util.UUID id) {
        super(String.format("%s with id %s not found", resource, id.toString()), HttpStatus.NOT_FOUND);
    }
}
