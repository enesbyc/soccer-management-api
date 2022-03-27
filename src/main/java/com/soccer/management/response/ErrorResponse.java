package com.soccer.management.response;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author enes.boyaci
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private String errorDescription;

    public ErrorResponse(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}
