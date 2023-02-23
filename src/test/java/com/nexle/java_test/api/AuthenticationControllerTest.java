package com.nexle.java_test.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexle.java_test.api.dto.*;
import com.nexle.java_test.api.service.AuthenticationService;
import com.nexle.java_test.common.response.APIResponse;
import com.nexle.java_test.exception.BadRequestException;
import lombok.var;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;


    private SignUpRequestDTO createSignUpRequestDTO() {
        SignUpRequestDTO responseDTO = new SignUpRequestDTO();
        responseDTO.setEmail("email@gmail.com");
        responseDTO.setPassword("password");
        responseDTO.setFirstName("firstName");
        responseDTO.setLastName("lastName");
        return responseDTO;
    }

    private SignUpResponseDTO createSignUpResponseDTO() {
        SignUpResponseDTO responseDTO = new SignUpResponseDTO();
        responseDTO.setEmail("email@gmail.com");
        responseDTO.setFirstName("firstName");
        responseDTO.setLastName("lastName");
        responseDTO.setDisplayName("firstName lastName");
        return responseDTO;
    }

    @Test
    void givenSignUpRequestDTO_whenSignUp_thenSuccess() throws Exception {
        SignUpRequestDTO requestDTO = createSignUpRequestDTO();
        SignUpResponseDTO responseDTO = createSignUpResponseDTO();

        APIResponse<SignUpResponseDTO> expectData = APIResponse.build(responseDTO, HttpStatus.CREATED);

        given(authenticationService.signUp(any(SignUpRequestDTO.class))).willReturn(responseDTO);

        var actual = mockMvc.perform(MockMvcRequestBuilders.post("/sign-up")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(requestDTO)))
                .andExpect(status().isCreated())
                .andReturn();
        assertEquals(asJsonString(expectData.getBody()), actual.getResponse().getContentAsString());
    }

    @Test
    void givenSignUpRequestDTO_whenSignUp_thenThrowBadRequest() throws Exception {
        SignUpRequestDTO requestDTO = createSignUpRequestDTO();
        given(authenticationService.signUp(any(SignUpRequestDTO.class))).willThrow(BadRequestException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/sign-up")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenSignInRequestDTO_whenSignIn_thenSuccess() throws Exception {
        SignInRequestDTO requestDTO = new SignInRequestDTO("email@gmail.com", "password");
        SignInResponseDTO responseDTO = new SignInResponseDTO();
        responseDTO.setRefreshToken("refreshToken");
        responseDTO.setToken("token");
        responseDTO.setUser(createSignUpResponseDTO());

        APIResponse<SignInResponseDTO> expectData = APIResponse.build(responseDTO, HttpStatus.OK);

        given(authenticationService.signIn(any(SignInRequestDTO.class))).willReturn(responseDTO);

        var actual = mockMvc.perform(MockMvcRequestBuilders.post("/sign-in")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(requestDTO)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        assertEquals(asJsonString(expectData.getBody()), actual.getResponse().getContentAsString());
    }

    @Test
    void givenSignInRequestDTO_whenSignIn_thenThrowBadRequestException() throws Exception {
        SignInRequestDTO requestDTO = new SignInRequestDTO();
        given(authenticationService.signIn(any(SignInRequestDTO.class))).willThrow(BadRequestException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/sign-up")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenRefreshTokenDTO_whenRefreshToken_thenSuccess() throws Exception {
        RefreshTokenDTO requestDTO = new RefreshTokenDTO();
        requestDTO.setRefreshToken("oldRefreshToken");
        RefreshTokenDTO responseDTO = new RefreshTokenDTO();
        responseDTO.setRefreshToken("refreshToken");
        responseDTO.setToken("token");

        APIResponse<RefreshTokenDTO> expectData = APIResponse.build(responseDTO, HttpStatus.OK);

        given(authenticationService.refreshToken(any(RefreshTokenDTO.class))).willReturn(responseDTO);

        var actual = mockMvc.perform(MockMvcRequestBuilders.post("/refresh-token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(requestDTO)))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        assertEquals(asJsonString(expectData.getBody()), actual.getResponse().getContentAsString());
    }

    @Test
    void givenRefreshTokenDTO_whenRefreshToken_thenThrowBadRequest() throws Exception {
        RefreshTokenDTO requestDTO = new RefreshTokenDTO();
        requestDTO.setRefreshToken("oldRefreshToken");

        given(authenticationService.refreshToken(any(RefreshTokenDTO.class))).willThrow(BadRequestException.class);

        mockMvc.perform(MockMvcRequestBuilders.post("/refresh-token")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(asJsonString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
