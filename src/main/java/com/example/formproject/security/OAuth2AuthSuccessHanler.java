package com.example.formproject.security;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.response.JwtResponseDto;
import com.example.formproject.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthSuccessHanler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final JwtProvider provider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        Member member = ((OAuth2User) authentication.getPrincipal()).getAttribute("member");
        JwtResponseDto token = provider.generateToken(member, member.getId());
        provider.setTokenHeader(token, response);
        getRedirectStrategy().sendRedirect(request, response, determineTargetUrl(token));
    }
// http://localhost:3000?member=jwttoken
    //token을 생성하고 이를 포함한 프론트엔드로의 uri를 생성한다.
    protected String determineTargetUrl(JwtResponseDto token) {
        String targetUri = FinalValue.REDIRECT_URL + "?";
        return UriComponentsBuilder.fromUriString(targetUri).queryParam("member", token).build().toUriString();
    }

}
