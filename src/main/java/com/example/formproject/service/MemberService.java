package com.example.formproject.service;

import com.example.formproject.annotation.UseCache;
import com.example.formproject.dto.request.LoginDto;
import com.example.formproject.dto.request.MemberInfoRequestDto;
import com.example.formproject.dto.request.MemberRequestDto;
import com.example.formproject.dto.response.JwtResponseDto;
import com.example.formproject.dto.response.MemberResponseDto;
import com.example.formproject.entity.Member;
import com.example.formproject.entity.RefreshToken;
import com.example.formproject.exception.AuthenticationException;
import com.example.formproject.repository.MemberRepository;
import com.example.formproject.repository.RefreshTokenRepository;
import com.example.formproject.security.JwtProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Getter
public class MemberService {
    private final JwtProvider provider;
    private final MemberRepository repository;
    private final BCryptPasswordEncoder encoder;
    private final AwsS3Service s3Service;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public JwtResponseDto login(LoginDto login) throws AuthenticationException {
        Member member = repository.findByEmail(login.getEmail()).orElseThrow(()->new AuthenticationException("계정을 찾을수 없습니다."));
        if(encoder.matches(login.getPassword(),member.getPassword())){
            JwtResponseDto jwtResponseDto = provider.generateToken(member, member.getId());
            RefreshToken refreshToken = new RefreshToken(jwtResponseDto, member.getId());
            refreshTokenRepository.save(refreshToken);
            return jwtResponseDto;
        }else{
            throw new AuthenticationException("계정 또는 비밀번호가 틀렸습니다.");
        }
    }
    public void save(MemberRequestDto dto){
        repository.save(dto.build(encoder));
    }

    @Transactional
    public ResponseEntity<?> updateMember(int memberid, MultipartFile profileImage, MemberInfoRequestDto requestDto, String username) {
        Member member = repository.findById(memberid).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않습니다."));
        String memberEmail = member.getEmail();
        if (Objects.equals(memberEmail, username)) {
            if (requestDto.getProfileImage() != null) {
                s3Service.deleteFile(requestDto.getProfileImage());
            }
            member.updateMember(requestDto, s3Service.uploadFile(profileImage));
            return new ResponseEntity<>("회원정보가 수정되었습니다.", HttpStatus.NO_CONTENT);
        }
        else return new ResponseEntity<>("회원정보 접근권한이 없습니다.", HttpStatus.FORBIDDEN);
    }
    @Transactional(readOnly = true)
    public MemberResponseDto makeMemberResponseDto(Member member){
        return new MemberResponseDto(member);
    }

}
