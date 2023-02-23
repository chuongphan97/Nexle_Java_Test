package com.nexle.java_test.common.response;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class APIResponse<T> extends ResponseEntity {

    public APIResponse(HttpStatus status) {
        this(APIBody.builder().code(status.value()).build(), null, status);
    }

    public APIResponse(T body, HttpStatus status) {
        this(APIBody.builder().code(status.value()).data(body).build(), null, status);
    }

    public APIResponse(int code, T body) {
        this(APIBody.builder().code(code).data(body).build(), null, HttpStatus.OK);
    }

    public APIResponse(int code, T body, HttpStatus httpStatus) {
        this(APIBody.builder().code(code).data(body).build(), null, httpStatus);
    }

    public APIResponse(int code, T body, MultiValueMap<String, String> headers, HttpStatus httpStatus) {
        this(APIBody.builder().code(code).data(body).build(), headers, httpStatus);
    }

    public APIResponse(APIBody body, MultiValueMap<String, String> headers, HttpStatus httpStatus) {
        super(body, headers, httpStatus);
    }

    public static <T> APIResponse<T> okStatus(T body) {
        return new APIResponse<T>(body, HttpStatus.OK);
    }

    public static <T> APIResponse<T> okStatus() {
        return new APIResponse<T>(HttpStatus.OK);
    }

    public static <T> APIResponse<T> build(T body, HttpStatus code) {
        return new APIResponse<T>(code.value(), body, code);
    }

    @Getter
    @Setter
    @Builder
    public static class APIBody<T> {
        int code;
        T data;
    }
}