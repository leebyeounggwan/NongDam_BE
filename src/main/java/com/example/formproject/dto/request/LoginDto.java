package com.example.formproject.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LoginDto {
    @Schema(type = "email",example = "example@abcd.com")
    private String email;
    @Schema(type = "String",example = "example password")
    private String password;
}
