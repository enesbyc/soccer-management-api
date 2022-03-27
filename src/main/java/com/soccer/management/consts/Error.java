package com.soccer.management.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author enes.boyaci
 */
@Getter
@AllArgsConstructor
public enum Error {
                   RESOURCE_NOT_FOUND("Resource not found."),
                   SYSTEM_ERROR("Unexpected system error occurred"),
                   INVALID_JWT_SIGNATURE("Invalid JWT signature"), INVALID_JWT("Invalid JWT"),
                   JWT_EXPIRED("JWT expired"), JWT_NOT_SUPPORTED("JWT is unsupported"),
                   UNAUTHORIZED("Unauthorized!"),
                   VALIDATION_FAILED("Validation failed. Please check the fields."),
                   RESOURCE_ALREADY_EXISTS("Resource already exists."),
                   BAD_CREDENTIAL("Please check username and password."),
                   BAD_REQUEST("Bad request");

    private final String description;
}
