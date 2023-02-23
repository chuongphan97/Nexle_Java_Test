package com.nexle.java_test.api.service;

import com.nexle.java_test.api.dto.*;
import com.nexle.java_test.exception.AuthenticationException;
import com.nexle.java_test.exception.BadRequestException;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    SignUpResponseDTO signUp(SignUpRequestDTO requestDTO) throws BadRequestException;

    SignInResponseDTO signIn(SignInRequestDTO requestDTO) throws BadRequestException, AuthenticationException;

    void signOut();

    RefreshTokenDTO refreshToken(RefreshTokenDTO requestDTO) throws BadRequestException;
}
