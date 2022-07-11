package com.example.formproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@Builder
@Getter
public class JwtResponseDto {
    @Schema(type = "String",example = "Bearer abcdefg...")
    private String token;
    @Schema(type = "String",example = "Bearer abcdefg...")
    private String refreshToken;

    public JwtResponseDto(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
