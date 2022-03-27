package com.soccer.management.exception;

import org.springframework.http.HttpStatus;

/**
 * @author enes.boyaci
 */
public class ResourceNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException() {
        super(HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
