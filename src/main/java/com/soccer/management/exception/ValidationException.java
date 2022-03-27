package com.soccer.management.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * @author enes.boyaci
 */
@Getter
public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private HttpStatus status;

    public ValidationException() {
        super(HttpStatus.BAD_REQUEST.getReasonPhrase());
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ValidationException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ValidationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
