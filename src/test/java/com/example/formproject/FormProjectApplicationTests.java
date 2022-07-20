package com.example.formproject;

import com.example.formproject.dto.request.LoginDto;
import com.example.formproject.dto.request.MemberRequestDto;
import com.example.formproject.dto.response.MemberResponseDto;
import com.example.formproject.repository.ImagesRepository;
import com.example.formproject.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class FormProjectApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private MemberRepository memberRepository;

    private HttpHeaders headers;

    private String jwtToken;

    private String refreshToken;

    private String email;
    private int userId;
    @BeforeEach
    void initHeader(){
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(this.jwtToken != null)
            headers.add("Authorization",jwtToken);
        if(this.refreshToken != null)
            headers.add("RefreshToken",refreshToken);
    }
    @Test
    void 작동테스트(){
        HttpEntity request = new HttpEntity(null,headers);
        ResponseEntity<String> response = restTemplate.exchange("/actuator/health",HttpMethod.GET,request,String.class);
        String body = response.getBody();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(body).contains("UP");
    }
    @Test
    @Order(1)
    void 회원가입() {
        MemberRequestDto dto = MemberRequestDto.builder()
                .email("qweasd3996@naver.com")
                .name("백규현")
                .nickname("nickname")
                .password("test")
                .build();
        HttpEntity request = new HttpEntity(dto,headers);
        ResponseEntity response = restTemplate.postForEntity("/member",request,String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        this.email = dto.getEmail();
    }

    @Test
    @Order(2)
    void 회원가입확인(){
        ResponseEntity response = restTemplate.getForEntity("/member/email?id=1",String.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(302);
    }
    @Test
    @Order(3)
    void 로그인(){
        LoginDto dto = new LoginDto(email,"test");
        HttpEntity request = new HttpEntity(dto,headers);
        ResponseEntity<String> response = restTemplate.postForEntity("/member/login",request,String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getHeaders().get("Authorization").get(0)).isNotNull();
        Assertions.assertThat(response.getHeaders().get("RefreshToken").get(0)).isNotNull();
        this.jwtToken = "Bearer "+response.getHeaders().get("Authorization").get(0);
        this.refreshToken = "Bearer "+response.getHeaders().get("RefreshToken").get(0);
    }
    @Test
    @Order(4)
    void 사용자정보_조회(){
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<MemberResponseDto> response = restTemplate.exchange("/member",HttpMethod.GET,request, MemberResponseDto.class);
        MemberResponseDto dto = response.getBody();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(dto.getProfileImage()).isEqualTo("https://idontcare.shop/static/default.png");
        Assertions.assertThat(dto.getEmail()).isEqualTo(email);
        Assertions.assertThat(dto.getId()).isNotNull();
        this.userId = dto.getId();
    }
}
