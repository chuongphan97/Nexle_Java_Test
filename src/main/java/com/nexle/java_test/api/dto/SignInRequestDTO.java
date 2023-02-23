package com.nexle.java_test.api.dto;

import com.nexle.java_test.utils.constants.APIConstants;
import com.nexle.java_test.utils.constants.Constants;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequestDTO {
    @Email(regexp = Constants.EMAIL_PATTERN, message = APIConstants.ERROR_EMAIL_NOT_CORRECT_FORMAT)
    @NotEmpty(message = APIConstants.ERROR_EMAIL_NOT_EMPTY)
    @ApiModelProperty(required = true)
    private String email;

    @Size(min = 8, max = 20, message = APIConstants.ERROR_PASSWORD_LENGTH)
    @NotEmpty(message = APIConstants.ERROR_PASSWORD_NOT_EMPTY)
    @ApiModelProperty(required = true)
    private String password;
}
