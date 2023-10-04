package com.foodmate.backend.exception;

import com.foodmate.backend.enums.Error;
import org.springframework.http.HttpStatus;

public class AuthException extends RuntimeException {

    private final Error error;
    private final String message;
    private final HttpStatus httpStatus;

    public AuthException(Error error) {
        this.error = error;
        this.message = error.getMessage();
        this.httpStatus = error.getHttpStatus();
    }

}