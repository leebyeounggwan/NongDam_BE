package com.example.formproject.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDto {
    @Schema(type = "email",example = "example@abcd.com")
    private String email;
    @Schema(type = "String",example = "example password")
    private String password;
}
