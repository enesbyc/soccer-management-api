package com.soccer.management.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author enes.boyaci
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseBuilder {

    public static ResponseEntity<ErrorResponse> build(ErrorResponse errorResponse,
                                                      HttpStatus status) {
        return new ResponseEntity<>(errorResponse, status);
    }

    public static <T> ResponseEntity<T> build(T item) {
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    public static <T> ResponseEntity<T> build(T item, HttpStatus status) {
        return new ResponseEntity<>(item, status);
    }

    public static <T> ResponseEntity<T> build(HttpStatus status) {
        return new ResponseEntity<>(status);
    }
}
