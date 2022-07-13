package com.example.formproject.dto.request;

import com.example.formproject.FinalValue;
import com.example.formproject.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberRequestDto {
    @Schema(type = "email", example = "example@abcd.com")
    private String email;
    @Schema(type = "String", example = "example password")
    private String password;
    @Schema(type = "String", example = "example nickname")
    private String nickname;
    @Schema(type = "String", example = "example name")
    private String name;

    public Member build(BCryptPasswordEncoder encoder) {
        return Member.builder()
                .email(email)
                .password(encoder.encode(password))
                .nickname(nickname)
                .profileImage(FinalValue.BACK_URL + "/static/default.png")
                .name(name)
                .build();
    }
}
