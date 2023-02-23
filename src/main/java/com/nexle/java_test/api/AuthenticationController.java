package com.nexle.java_test.api;

import com.nexle.java_test.api.dto.*;
import com.nexle.java_test.api.service.AuthenticationService;
import com.nexle.java_test.common.response.APIResponse;
import com.nexle.java_test.exception.AuthenticationException;
import com.nexle.java_test.exception.BadRequestException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;


    private static final String SIGN_UP_ENDPOINT = "/sign-up";
    private static final String SIGN_IN_ENDPOINT = "/sign-in";
    private static final String SIGN_OUT_ENDPOINT = "/sign-out";
    private static final String REFRESH_TOKEN_ENDPOINT = "/refresh-token";

    @ApiOperation("API for sign up")
    @PostMapping(SIGN_UP_ENDPOINT)
    public APIResponse<SignUpResponseDTO> signUp(
            @ApiParam(value = "User sign up information")
            @Valid
            @RequestBody SignUpRequestDTO requestDTO) throws BadRequestException {
        return APIResponse.build(authenticationService.signUp(requestDTO), HttpStatus.CREATED);
    }

    @ApiOperation("API for sign in")
    @PostMapping(SIGN_IN_ENDPOINT)
    public APIResponse<SignInResponseDTO> signIn(
            @ApiParam(value = "User sign in information")
            @Valid
            @RequestBody SignInRequestDTO requestDTO) throws AuthenticationException, BadRequestException {
        return APIResponse.build(authenticationService.signIn(requestDTO), HttpStatus.OK);
    }

    @ApiOperation("API for sign out")
    @PostMapping(SIGN_OUT_ENDPOINT)
    public APIResponse<Void> signOut() {
        authenticationService.signOut();
        return APIResponse.build(null, HttpStatus.NO_CONTENT);
    }

    @ApiOperation("API for refresh token")
    @PostMapping(REFRESH_TOKEN_ENDPOINT)
    public APIResponse<RefreshTokenDTO> refreshToken(@Valid @RequestBody RefreshTokenDTO requestDTO) throws BadRequestException {
        return APIResponse.okStatus(authenticationService.refreshToken(requestDTO));
    }
}