package com.example.formproject.dto.response;

import lombok.*;

@NoArgsConstructor
@Builder
@Getter
public class JwtResponseDto {
    private String token;
    private String refreshToken;

    public JwtResponseDto(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
