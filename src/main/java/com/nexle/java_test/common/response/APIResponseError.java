package com.nexle.java_test.common.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * A value object representing a api error response data
 */
@Getter
@Setter
@Builder
public class APIResponseError {
    public long timestamp;
    public int code;
    @JsonInclude(value = Include.NON_NULL)
    public String error;
    public Object message;


    public APIResponseError() {
    }

    public APIResponseError(int code, Object message) {
        this.timestamp = new Date().getTime();
        this.code = code;
        this.message = message;
    }

    public APIResponseError(int code, Object message, String error) {
        this.timestamp = new Date().getTime();
        this.code = code;
        this.message = message;
        this.error = error;
    }

    public APIResponseError(long timestamp, int code, String error, Object message) {
        this.timestamp = new Date().getTime();
        this.code = code;
        this.error = error;
        this.message = message;
    }
}