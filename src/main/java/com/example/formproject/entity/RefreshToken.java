package com.example.formproject.entity;

import com.example.formproject.dto.response.JwtResponseDto;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String jwtToken;
    private String refreshToken;
    private int memberId;

    public RefreshToken(JwtResponseDto jwtResponseDto, int memberId) {
        this.jwtToken = jwtResponseDto.getToken();
        this.refreshToken = jwtResponseDto.getRefreshToken();
        this.memberId = memberId;
    }
}
