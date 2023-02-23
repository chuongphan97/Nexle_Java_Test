package com.nexle.java_test.api.service;

import com.nexle.java_test.api.dto.*;
import com.nexle.java_test.common.entity.Token;
import com.nexle.java_test.common.entity.User;
import com.nexle.java_test.common.repository.TokenRepository;
import com.nexle.java_test.common.repository.UserRepository;
import com.nexle.java_test.common.service.BaseService;
import com.nexle.java_test.config.jwt.AccessTokenDTO;
import com.nexle.java_test.config.jwt.JwtProvider;
import com.nexle.java_test.config.jwt.UserPrincipal;
import com.nexle.java_test.config.security.BaseUserDetailsService;
import com.nexle.java_test.exception.AuthenticationException;
import com.nexle.java_test.exception.BadRequestException;
import com.nexle.java_test.utils.constants.APIConstants;
import com.nexle.java_test.utils.constants.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl extends BaseService implements AuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public SignUpResponseDTO signUp(SignUpRequestDTO requestDTO) throws BadRequestException {
        Optional<User> userOptional = userRepository.findByEmail(requestDTO.getEmail());
        if (userOptional.isPresent()) {
            throw new BadRequestException(BadRequestException.ERROR_USER_ALREADY_EXIST, APIConstants.ERROR_USER_ALREADY_EXIST);
        }
        User user = new User();
        user.setPassword(bCryptPasswordEncoder.encode(requestDTO.getPassword()));
        user.setEmail(requestDTO.getEmail());
        user.setFirstName(requestDTO.getFirstName());
        user.setLastName(requestDTO.getLastName());
        return mapToSignUpResponseDTO(userRepository.save(user));
    }

    @Override
    public SignInResponseDTO signIn(SignInRequestDTO requestDTO) throws BadRequestException, AuthenticationException {
        User user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new BadRequestException(BadRequestException.ERROR_USER_NOT_FOUND, APIConstants.ERROR_USER_NOT_FOUND));
        if (!bCryptPasswordEncoder.matches(requestDTO.getPassword(), user.getPassword())) {
            throw new AuthenticationException(AuthenticationException.UNAUTHORIZED_INVALID_PASSWORD,
                    APIConstants.PASSWORD_INPUT_IS_INVALID, false);
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(UserPrincipal.create(user, BaseUserDetailsService.getAuthorities()), null, BaseUserDetailsService.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AccessTokenDTO accessToken = jwtProvider.createAccessToken(authentication);
        tokenRepository.save(createTokenForUser(user, accessToken));
        //hide userId
        user.setId(null);
        return new SignInResponseDTO(mapToSignUpResponseDTO(user), accessToken.getToken(), accessToken.getRefreshToken());
    }

    @Override
    @Transactional
    public void signOut() {
        Integer userId = getCurrentUserId();
        tokenRepository.deleteAllByUserId(userId);
    }

    @Override
    @Transactional
    public RefreshTokenDTO refreshToken(RefreshTokenDTO requestDTO) throws BadRequestException {
        Token token = tokenRepository.findByRefreshToken(requestDTO.getRefreshToken())
                .orElseThrow(() -> new BadRequestException(BadRequestException.ERROR_REFRESH_TOKEN_NOT_FOUND, APIConstants.ERROR_REFRESH_TOKEN_NOT_FOUND, HttpStatus.NOT_FOUND));
        Authentication authentication = new UsernamePasswordAuthenticationToken(UserPrincipal.create(token.getUser(), BaseUserDetailsService.getAuthorities()), null, BaseUserDetailsService.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AccessTokenDTO accessToken = jwtProvider.createAccessToken(authentication);

        //Update refresh token
        token.setRefreshToken(accessToken.getRefreshToken());
        token.setExpiresIn(accessToken.getRefreshTokenExpiredIn().toString());

        return new RefreshTokenDTO(accessToken.getToken(), accessToken.getRefreshToken());
    }

    private Token createTokenForUser(User user, AccessTokenDTO accessTokenDTO) {
        Token token = new Token();
        token.setUser(user);
        token.setRefreshToken(accessTokenDTO.getRefreshToken());
        token.setExpiresIn(accessTokenDTO.getTokenExpiredIn().toString());
        return token;
    }

    private SignUpResponseDTO mapToSignUpResponseDTO(User user) {
        SignUpResponseDTO responseDTO = new SignUpResponseDTO();
        BeanUtils.copyProperties(user, responseDTO);
        responseDTO.setDisplayName(null != user.getFirstName() && null != user.getLastName() ? user.getFirstName() + Constants.SPACE + user.getLastName() :
                (null != user.getFirstName() ? user.getFirstName() : user.getLastName()));
        return responseDTO;
    }
}
