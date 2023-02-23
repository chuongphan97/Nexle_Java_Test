package com.nexle.java_test.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BadRequestException extends Exception {
    public static final String ERROR_USER_ALREADY_EXIST = "ERROR_USER_ALREADY_EXIST";
    public static final String ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND";
    public static final String ERROR_REFRESH_TOKEN_NOT_FOUND = "ERROR_REFRESH_TOKEN_NOT_FOUND";
    private static final long serialVersionUID = 1L;

    private String error;
    private String message;

    private HttpStatus httpStatus;

    @JsonIgnore
    private boolean isPrintStackTrace;

    public BadRequestException() {
        super();
    }

    public BadRequestException(String error, String message) {
        super(message);
        this.error = error;
        this.message = message;
    }

    public BadRequestException(String error, String message, HttpStatus httpStatus) {
        super(message);
        this.error = error;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
