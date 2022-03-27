package com.soccer.management.exception;

import org.springframework.http.HttpStatus;

/**
 * @author enes.boyaci
 */
public class BadRequestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

    public BadRequestException(String message) {
        super(message);
    }
}
