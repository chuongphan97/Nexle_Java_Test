package com.nexle.java_test.exception;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/**
 * Authentication Exception
 */
@Getter
@Setter
@AllArgsConstructor
public class AuthenticationException extends Exception {

    public static final String UNAUTHORIZED_USER_NOT_FOUND = "UNAUTHORIZED_USER_NOT_FOUND";
    public static final String UNAUTHORIZED_USER_INACTIVE = "UNAUTHORIZED_USER_INACTIVE";
    public static final String UNAUTHORIZED_USER_BLOCKED = "UNAUTHORIZED_USER_BLOCKED";
    public static final String UNAUTHORIZED_USER_DELETED = "UNAUTHORIZED_USER_DELETED";
    public static final String UNAUTHORIZED_INVALID_EMAIL_OR_PASSWORD = "UNAUTHORIZED_INVALID_EMAIL_OR_PASSWORD";
    public static final String UNAUTHORIZED_INVALID_USER_ID = "UNAUTHORIZED_INVALID_USER_ID";
    public static final String UNAUTHORIZED_INVALID_PASSWORD = "UNAUTHORIZED_INVALID_PASSWORD";
    public static final String ERROR_JWT_INVALID_TOKEN = "ERROR_JWT_INVALID_TOKEN";


    /**
     * SyTV updated Validate input data from user
     */
    public static final String ERROR_REQUIRED_USER_INFORMATION_CAN_NOT_NULL = "ERROR_REQUIRED_USER_INFORMATION_CAN_NOT_NULL";

    private static final long serialVersionUID = 1L;
    private int code;
    private String error;
    private String message;
    private HttpStatus httpStatus;
    @JsonIgnore
    private boolean isPrintStackTrace;

    public AuthenticationException() {
    }

    public AuthenticationException(String error, String message) {
        super(message);
        this.error = error;
        this.message = message;
    }

    public AuthenticationException(String error, String message, boolean isPrintStackTrace) {
        super(message);
        this.error = error;
        this.message = message;
        this.isPrintStackTrace = isPrintStackTrace;
    }
}