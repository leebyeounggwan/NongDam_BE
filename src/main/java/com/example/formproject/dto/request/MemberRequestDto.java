package com.example.formproject.dto.request;

import com.example.formproject.entity.Member;
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
    private String email;
    private String password;
    private String nickname;
    private String name;
    public Member build(BCryptPasswordEncoder encoder){
        return Member.builder()
                .email(email)
                .password(encoder.encode(password))
                .nickname(nickname)
                .name(name)
                .build();
    }
}
