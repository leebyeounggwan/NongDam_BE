package com.example.formproject.service;

import com.example.formproject.annotation.DeleteMemberCache;
import com.example.formproject.annotation.UseCache;
import com.example.formproject.dto.request.LoginDto;
import com.example.formproject.dto.request.MailDto;
import com.example.formproject.dto.request.MemberInfoRequestDto;
import com.example.formproject.dto.request.MemberRequestDto;
import com.example.formproject.dto.response.JwtResponseDto;
import com.example.formproject.dto.response.MemberResponseDto;
import com.example.formproject.entity.Member;
import com.example.formproject.entity.RefreshToken;
import com.example.formproject.exception.AuthenticationException;
import com.example.formproject.exception.EmailConfirmException;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.repository.MemberRepository;
import com.example.formproject.repository.RefreshTokenRepository;
import com.example.formproject.security.JwtProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
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

    private final CropRepository cropRepository;

    private final EmailService emailService;

    @Transactional
    public JwtResponseDto login(LoginDto login, HttpServletResponse response) throws AuthenticationException, EmailConfirmException {
        Member member = repository.findByEmail(login.getEmail()).orElseThrow(()->new AuthenticationException("계정을 찾을수 없습니다."));
        if(encoder.matches(login.getPassword(),member.getPassword())){
            JwtResponseDto jwtResponseDto = provider.generateToken(member, response);
            if(member.isLock())
                throw new EmailConfirmException("이메일 인증이 필요한 계정입니다.");
            return jwtResponseDto;
        } else {
            throw new AuthenticationException("계정 또는 비밀번호가 틀렸습니다.");
        }
    }

    @Transactional
    public void enableMember(int id) throws Exception {
        Member member = repository.findById(id).orElseThrow(()->new Exception("계정을 찾을 수 없습니다."));
        member.enableId();
        repository.save(member);
        System.out.println("member is Enable :" + member.getEmail());
    }

    public void save(MemberRequestDto dto) throws MessagingException {
        Member member = repository.save(dto.build(encoder));
        emailService.sendHtmlEmail(MailDto.builder().email(dto.getEmail()).build(),member);
    }

    @Transactional
    @DeleteMemberCache(memberIdArg = "memberid")
    public ResponseEntity<?> updateMember(int memberid, MultipartFile profileImage, MemberInfoRequestDto requestDto, String username) {
        Member member = repository.findById(memberid).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않습니다."));
        String memberEmail = member.getEmail();
        if (Objects.equals(memberEmail, username)) {
            if (profileImage != null)
                member.updateMember(requestDto, s3Service.uploadFile(profileImage), cropRepository);
            else {
                s3Service.deleteFile(member.getProfileImage());
                member.updateMember(requestDto, cropRepository);
            }
            return new ResponseEntity<>("회원정보가 수정되었습니다.", HttpStatus.NO_CONTENT);
        } else return new ResponseEntity<>("회원정보 접근권한이 없습니다.", HttpStatus.FORBIDDEN);
    }

    @Transactional(readOnly = true)
    public MemberResponseDto makeMemberResponseDto(Member member) {
        return new MemberResponseDto(member);
    }
}