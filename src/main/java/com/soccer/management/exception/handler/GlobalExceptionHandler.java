package com.soccer.management.exception.handler;

import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.soccer.management.consts.Error;
import com.soccer.management.exception.BadRequestException;
import com.soccer.management.exception.ResourceAlreadyExistsException;
import com.soccer.management.exception.ResourceNotFoundException;
import com.soccer.management.exception.ValidationException;
import com.soccer.management.response.ErrorResponse;
import com.soccer.management.response.ResponseBuilder;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author enes.boyaci
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<ErrorResponse> validationError(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        fieldErrors.toArray(new FieldError[fieldErrors.size()]);

        StringBuilder builder = new StringBuilder();
        for (FieldError error : result.getFieldErrors()) {
            builder.append(error.getDefaultMessage());
        }
        return ResponseBuilder.build(new ErrorResponse(builder.toString()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> baseExceptionHandler(Exception exception) {
        log.error("An unexpected error has occurred. Detail:", exception);
        return ResponseBuilder.build(new ErrorResponse(Error.SYSTEM_ERROR.getDescription()),
                                     HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> signatureExceptionHandler(SignatureException exception) {
        log.error("Invalid JWT signature. Detail:", exception);
        return ResponseBuilder
                        .build(new ErrorResponse(Error.INVALID_JWT_SIGNATURE.getDescription()),
                               HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> mapformedJwtException(MalformedJwtException exception) {
        log.error("Invalid JWT. Detail:", exception);
        return ResponseBuilder.build(new ErrorResponse(Error.INVALID_JWT.getDescription()),
                                     HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> expiredJwtExceptionHandler(ExpiredJwtException exception) {
        log.error("Expired JWT. Detail:", exception);
        return ResponseBuilder.build(new ErrorResponse(Error.JWT_EXPIRED.getDescription()),
                                     HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ErrorResponse> unsupportedJwtExceptionHandler(UnsupportedJwtException exception) {
        log.error("JWT not supported. Detail:", exception);
        return ResponseBuilder.build(new ErrorResponse(Error.JWT_NOT_SUPPORTED.getDescription()),
                                     HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> badCredentialsExceptionHandler(BadCredentialsException exception) {
        log.error("Validation failed. Detail:", exception);
        return ResponseBuilder.build(new ErrorResponse(Error.BAD_CREDENTIAL.getDescription()),
                                     HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> resourceAlreadyExistsExceptionHandler(ResourceAlreadyExistsException exception) {
        log.error("Resource already exists. Detail:", exception);
        return ResponseBuilder.build(new ErrorResponse(
                        Objects.nonNull(exception.getMessage()) ? exception.getMessage()
                                                                : Error.RESOURCE_ALREADY_EXISTS
                                                                                .getDescription()),
                                     HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundExceptionHandler(ResourceNotFoundException exception) {
        log.error("Resource not found. Detail:", exception);
        return ResponseBuilder.build(new ErrorResponse(
                        Objects.nonNull(exception.getMessage()) ? exception.getMessage()
                                                                : Error.RESOURCE_NOT_FOUND
                                                                                .getDescription()),
                                     HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> validationExceptionHandler(ValidationException exception) {
        log.error("Validation exception. Detail:", exception);
        return ResponseBuilder.build(new ErrorResponse(
                        Objects.nonNull(exception.getMessage()) ? exception.getMessage()
                                                                : Error.VALIDATION_FAILED
                                                                                .getDescription()),
                                     HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> badRequestExceptionHandler(BadRequestException exception) {
        log.error("Bad request exception. Detail:", exception);
        return ResponseBuilder.build(new ErrorResponse(
                        Objects.nonNull(exception.getMessage()) ? exception.getMessage()
                                                                : Error.BAD_REQUEST
                                                                                .getDescription()),
                                     HttpStatus.BAD_REQUEST);
    }

}
