package com.nexle.java_test.service;

import com.nexle.java_test.api.dto.*;
import com.nexle.java_test.api.service.AuthenticationServiceImpl;
import com.nexle.java_test.common.entity.Token;
import com.nexle.java_test.common.entity.User;
import com.nexle.java_test.common.repository.TokenRepository;
import com.nexle.java_test.common.repository.UserRepository;
import com.nexle.java_test.config.jwt.AccessTokenDTO;
import com.nexle.java_test.config.jwt.JwtProvider;
import com.nexle.java_test.exception.AuthenticationException;
import com.nexle.java_test.exception.BadRequestException;
import com.nexle.java_test.utils.constants.APIConstants;
import com.nexle.java_test.utils.constants.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthenticationServiceTest {
    @Mock
    private  UserRepository userRepository;

    @Mock
    private  TokenRepository tokenRepository;

    @Mock
    private  BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private  JwtProvider jwtProvider;

    @Spy
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private SignUpRequestDTO createSignUpRequestDTO() {
        SignUpRequestDTO responseDTO = new SignUpRequestDTO();
        responseDTO.setEmail("email@gmail.com");
        responseDTO.setPassword("password");
        responseDTO.setFirstName("firstName");
        responseDTO.setLastName("lastName");
        return responseDTO;
    }

    private SignInRequestDTO createSignInRequestDTO() {
        return new SignInRequestDTO("email@gmail.com", "password");
    }

    private AccessTokenDTO createAccessTokenDTO() {
        AccessTokenDTO response = new AccessTokenDTO();
        response.setRefreshToken("refreshToken");
        response.setToken("token");
        response.setTokenType("tokenType");
        response.setTokenExpiredIn(new Date());
        response.setRefreshTokenExpiredIn(new Date());
        response.setUserId(1);
        return response;
    }

    private User createUser() {
        User user = new User();
        user.setId(1);
        user.setEmail("email@gmail.com");
        user.setPassword("password");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        return user;
    }

    private Token createToken() {
        Token response = new Token();
        response.setUser(createUser());
        response.setId(1);
        response.setRefreshToken("refreshToken");
        response.setExpiresIn(new Date().toString());
        return response;
    }

    @Test
    void givenSignUpRequestDTO_whenSignUp_thenSuccess() throws BadRequestException {
        SignUpRequestDTO requestDTO = createSignUpRequestDTO();
        User user = createUser();

        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(userRepository.save(any(User.class))).willReturn(user);

        SignUpResponseDTO actual = authenticationService.signUp(requestDTO);

        assertEquals(actual.getEmail(), requestDTO.getEmail());
        assertEquals(actual.getFirstName(), requestDTO.getFirstName());
        assertEquals(actual.getLastName(), requestDTO.getLastName());
        assertEquals(actual.getId(), user.getId());
        assertEquals(actual.getDisplayName(), requestDTO.getFirstName() + Constants.SPACE + requestDTO.getLastName());

        verify(userRepository).findByEmail(anyString());
        verify(userRepository).save(any(User.class));
        verify(bCryptPasswordEncoder).encode(requestDTO.getPassword());
    }

    @Test
    void givenSignUpRequestDTO_whenSignUp_thenThrowBadRequest() {
        SignUpRequestDTO requestDTO = createSignUpRequestDTO();
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(createUser()));

        Exception exception = assertThrows(BadRequestException.class, () -> authenticationService.signUp(requestDTO));

        assertThat(exception.getMessage()).isEqualTo(APIConstants.ERROR_USER_ALREADY_EXIST);
        verify(userRepository).findByEmail(anyString());
    }

    @Test
    void givenSignInRequestDTO_whenSignIn_thenSuccess() throws AuthenticationException, BadRequestException {
        User user = createUser();
        SignInRequestDTO requestDTO = createSignInRequestDTO();
        AccessTokenDTO accessToken = createAccessTokenDTO();
        Token token = createToken();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(requestDTO.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtProvider.createAccessToken(any(Authentication.class))).thenReturn(accessToken);
        when(tokenRepository.save(any(Token.class))).thenReturn(token);

        SignInResponseDTO actual = authenticationService.signIn(requestDTO);

        assertEquals(actual.getRefreshToken(), accessToken.getRefreshToken());
        assertNull(actual.getUser().getId());
        assertEquals(actual.getUser().getEmail(), user.getEmail());
        assertEquals(actual.getUser().getFirstName(), user.getFirstName());
        assertEquals(actual.getUser().getLastName(), user.getLastName());
        assertEquals(actual.getUser().getDisplayName(), user.getFirstName() + Constants.SPACE + user.getLastName());
        assertEquals(actual.getToken(), accessToken.getToken());
        assertEquals(actual.getRefreshToken(), accessToken.getRefreshToken());
    }

    @Test
    void givenSignInRequestDTO_whenSignIn_thenThrowBadRequestUserNotFound() {
        SignInRequestDTO requestDTO = createSignInRequestDTO();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        Exception exception = assertThrows(BadRequestException.class, () -> authenticationService.signIn(requestDTO));

        assertThat(exception.getMessage()).isEqualTo(APIConstants.ERROR_USER_NOT_FOUND);
        verify(userRepository).findByEmail(anyString());
    }

    @Test
    void givenSignInRequestDTO_whenSignIn_thenThrowUnAuthorizedInvalidPassword() {
        SignInRequestDTO requestDTO = createSignInRequestDTO();
        User user = createUser();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(bCryptPasswordEncoder.matches(requestDTO.getPassword(), user.getPassword())).thenReturn(false);

        Exception exception = assertThrows(AuthenticationException.class, () -> authenticationService.signIn(requestDTO));

        assertThat(exception.getMessage()).isEqualTo(APIConstants.PASSWORD_INPUT_IS_INVALID);
        verify(userRepository).findByEmail(anyString());
        verify(bCryptPasswordEncoder).matches(requestDTO.getPassword(), user.getPassword());
    }

    @Test
    void whenSignOut_thenSuccess() {
        Integer userId = 1;

        doReturn(userId).when(authenticationService).getCurrentUserId();

        authenticationService.signOut();

        verify(tokenRepository).deleteAllByUserId(userId);
    }

    @Test
    void givenRefreshTokenDTO_whenRefreshToken_thenSuccess() throws BadRequestException {
        RefreshTokenDTO requestDTO = new RefreshTokenDTO();
        requestDTO.setRefreshToken("oldRefreshToken");
        Token token = createToken();
        AccessTokenDTO accessTokenDTO = createAccessTokenDTO();

        when(tokenRepository.findByRefreshToken(requestDTO.getRefreshToken())).thenReturn(Optional.of(token));
        when(jwtProvider.createAccessToken(any(Authentication.class))).thenReturn(accessTokenDTO);

        RefreshTokenDTO actual = authenticationService.refreshToken(requestDTO);

        assertNotEquals(actual.getRefreshToken(), requestDTO.getRefreshToken());
        assertEquals(token.getRefreshToken(), actual.getRefreshToken());
    }

    @Test
    void givenRefreshTokenDTO_whenRefreshToken_thenThrowBadRequest() {
        RefreshTokenDTO requestDTO = new RefreshTokenDTO();
        requestDTO.setRefreshToken("oldRefreshToken");
        when(tokenRepository.findByRefreshToken("oldRefreshToken")).thenReturn(Optional.empty());

        Exception exception = assertThrows(BadRequestException.class, () -> authenticationService.refreshToken(requestDTO));

        assertThat(exception.getMessage()).isEqualTo(APIConstants.ERROR_REFRESH_TOKEN_NOT_FOUND);
        verify(tokenRepository).findByRefreshToken("oldRefreshToken");
    }

}
