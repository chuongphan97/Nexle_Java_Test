package com.nexle.java_test.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponseDTO {
    private SignUpResponseDTO user;

    private String token;

    private String refreshToken;
}
