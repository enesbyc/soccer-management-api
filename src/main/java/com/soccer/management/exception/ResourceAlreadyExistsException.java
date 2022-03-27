package com.soccer.management.exception;

import org.springframework.http.HttpStatus;

/**
 * @author enes.boyaci
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceAlreadyExistsException() {
        super(HttpStatus.CONFLICT.getReasonPhrase());
    }

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

}
