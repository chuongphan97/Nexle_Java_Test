package com.nexle.java_test.config.jwt;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
public class AccessTokenDTO {
    @ApiModelProperty(value = "Access token", readOnly = true)
    private String token;
    @ApiModelProperty(value = "Access token expired date", readOnly = true)
    private Date tokenExpiredIn;

    @ApiModelProperty(value = "Refresh token")
    @NotNull(message = "Refresh token not null")
    @NotEmpty(message = "Refresh token not empty")
    private String refreshToken;

    @ApiModelProperty(value = "Access token expired date", readOnly = true)
    private Date refreshTokenExpiredIn;

    @ApiModelProperty(value = "User Id", readOnly = true)
    private Integer userId;

    @ApiModelProperty(readOnly = true)
    private String tokenType;
}