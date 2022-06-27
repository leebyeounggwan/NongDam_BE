package com.example.formproject.service;

import com.example.formproject.dto.request.LoginDto;
import com.example.formproject.dto.request.MemberRequestDto;
import com.example.formproject.entity.Member;
import com.example.formproject.exception.AuthenticationException;
import com.example.formproject.repository.MemberRepository;
import com.example.formproject.security.JwtProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class MemberService {
    private final JwtProvider provider;
    private final MemberRepository repository;
    private final BCryptPasswordEncoder encoder;

    public String login(LoginDto login) throws AuthenticationException {
        Member member = repository.findByEmail(login.getEmail()).orElseThrow(()->new AuthenticationException("계정을 찾을수 없습니다."));
        if(encoder.matches(login.getPassword(),member.getPassword())){
            return provider.generateToken(member);
        }else{
            throw new AuthenticationException("계정 또는 비밀번호가 틀렸습니다.");
        }
    }
    public void save(MemberRequestDto dto){
        repository.save(dto.build(encoder));
    }
}
