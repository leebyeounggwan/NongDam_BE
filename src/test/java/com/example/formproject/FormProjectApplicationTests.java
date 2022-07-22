package com.example.formproject;

import com.example.formproject.dto.request.*;
import com.example.formproject.dto.response.*;
import com.example.formproject.enums.AccountType;
import com.example.formproject.enums.CountryCode;
import com.example.formproject.enums.SubMaterialType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class FormProjectApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;

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
    @Order(0)
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
    @Test
    @Order(5)
    void 개인정보_수정() throws JsonProcessingException {
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MemberInfoRequestDto dto = MemberInfoRequestDto.builder()
                .address("경기도 수원시 권선구 고색동")
                .countryCode(CountryCode.수원.getType())
                .nickname("변경한 닉네임")
                .crops(Arrays.asList(new Integer[]{1,2,3}))
                .build();
        ObjectMapper mapper = new ObjectMapper();
        MultiValueMap<String,Object> parameter = new LinkedMultiValueMap<>();
        parameter.add("data",mapper.writer().writeValueAsString(dto));

        HttpEntity request = new HttpEntity(parameter,headers);
        ResponseEntity<String> response = restTemplate.exchange("/member",HttpMethod.PUT,request,String.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
    @Test
    @Order(6)
    void 수정된_개인정보_확인() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<MemberResponseDto> response = restTemplate.exchange("/member", HttpMethod.GET, request, MemberResponseDto.class);
        MemberResponseDto dto = response.getBody();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(dto.getAddress()).isEqualTo("경기도 수원시 권선구 고색동");
        Assertions.assertThat(dto.getNickname()).isEqualTo("변경한 닉네임");
        for(int i = 1; i < 4; i++) {
            int finalI = i;
            Assertions.assertThat(dto.getCrops().stream().filter(e->e.getId()== finalI).findFirst().orElse(null)).isNotNull();
        }
    }

    @Test
    void 장부_Controller(){
        // 장부 기록 생성
        LocalDate now = LocalDate.now();
        AccountRequestDto dto = AccountRequestDto.builder()
                .type(AccountType.고용노동비.ordinal())
                .memo("test")
                .price(1000)
                .date(LocalDate.now().format(FinalValue.DAY_FORMATTER)).build();
        HttpEntity request = new HttpEntity(dto,headers);
        ResponseEntity<AccountResponseDto> response = restTemplate.postForEntity("/accountbook",request,AccountResponseDto.class);
        AccountResponseDto originalDto = response.getBody();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(originalDto.getId()).isNotNull();
        Assertions.assertThat(originalDto.getType()).isEqualTo(dto.getType());
        Assertions.assertThat(originalDto.getMemo()).isEqualTo(dto.getMemo());
        Assertions.assertThat(originalDto.getDate()).isEqualTo(dto.getDate());
        Assertions.assertThat(originalDto.getPrice()).isEqualTo(dto.getPrice());


        AccountRequestDto dto2 = AccountRequestDto.builder()
                .type(AccountType.농산물_판매.ordinal())
                .memo("test")
                .price(10000)
                .date(LocalDate.now().format(FinalValue.DAY_FORMATTER)).build();
        request = new HttpEntity(dto2,headers);
        response = restTemplate.postForEntity("/accountbook",request,AccountResponseDto.class);
        AccountResponseDto originalDto2 = response.getBody();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(originalDto2.getId()).isNotNull();
        Assertions.assertThat(originalDto2.getType()).isEqualTo(dto2.getType());
        Assertions.assertThat(originalDto2.getMemo()).isEqualTo(dto2.getMemo());
        Assertions.assertThat(originalDto2.getDate()).isEqualTo(dto2.getDate());
        Assertions.assertThat(originalDto2.getPrice()).isEqualTo(dto2.getPrice());
        long accountId = originalDto.getId();
        // 장부 기록 수정
        dto = AccountRequestDto.builder()
                .type(AccountType.고용노동비.ordinal())
                .memo("ttest")
                .price(3000)
                .date(LocalDate.now().format(FinalValue.DAY_FORMATTER)).build();
        request = new HttpEntity(dto,headers);
        response = restTemplate.exchange("/accountbook/"+accountId,HttpMethod.PUT,request,AccountResponseDto.class);
        AccountResponseDto changedDto = response.getBody();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(changedDto.getId()).isNotNull();
        Assertions.assertThat(changedDto.getType()).isEqualTo(dto.getType());
        Assertions.assertThat(changedDto.getMemo()).isEqualTo(dto.getMemo());
        Assertions.assertThat(changedDto.getDate()).isEqualTo(dto.getDate());
        Assertions.assertThat(changedDto.getPrice()).isEqualTo(dto.getPrice());

        // 월별 장부 조회
        request = new HttpEntity(headers);
        ResponseEntity<List<AccountResponseDto>> listResponse = restTemplate.exchange("/accountbook/" + now.getYear() + "-" + now.getMonthValue(),HttpMethod.GET,request, new ParameterizedTypeReference<List<AccountResponseDto>>() {});
        Assertions.assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 일주일치 장부 조회
//        request = new HttpEntity(headers);
        listResponse = restTemplate.exchange("/accountbook" ,HttpMethod.GET,request, new ParameterizedTypeReference<List<AccountResponseDto>>() {});
        Assertions.assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 수입 통계
        ResponseEntity<CircleChartDto> circleResponse = restTemplate.exchange("/income",HttpMethod.GET,request,CircleChartDto.class);
        Assertions.assertThat(circleResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 지출통계
        circleResponse = restTemplate.exchange("/expense",HttpMethod.GET,request,CircleChartDto.class);
        Assertions.assertThat(circleResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 연도별 수입/지출 통계
        ResponseEntity<LineChartDto> lineResponse = restTemplate.exchange("/sales/year",HttpMethod.GET,request,LineChartDto.class);
        Assertions.assertThat(lineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 월별 수입/지출 통계
        lineResponse = restTemplate.exchange("/sales/month",HttpMethod.GET,request,LineChartDto.class);
        Assertions.assertThat(lineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 장부 삭제
//        request = new HttpEntity(headers);
        ResponseEntity<String> deleteResponse = restTemplate.exchange("/accountbook/"+originalDto.getId(),HttpMethod.DELETE,request,String.class);
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        deleteResponse = restTemplate.exchange("/accountbook/"+originalDto2.getId(),HttpMethod.DELETE,request,String.class);
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void 기준정보_Controller(){
        HttpEntity request = new HttpEntity(headers);
        // 전체 작물조회
        ResponseEntity<List<CropDto>> response = restTemplate.exchange("/crops", HttpMethod.GET, request, new ParameterizedTypeReference<List<CropDto>>() {
        });
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 개인 작물 조회
        response = restTemplate.exchange("/crop",HttpMethod.GET,request, new ParameterizedTypeReference<List<CropDto>>() {
        });
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 뉴스 API
        ResponseEntity<List<NewsResponseDto>> newsResponse = restTemplate.exchange("/news", HttpMethod.GET, request, new ParameterizedTypeReference<List<NewsResponseDto>>() {
        });
        Assertions.assertThat(newsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    void 일정_Controller(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime to = now.plusHours(1L);
        // 일정 생성
        ScheduleRequestDto dto = ScheduleRequestDto.builder()
                .cropId(1)
                .toDo("test ToDo")
                .startTime(now.format(FinalValue.DAYTIME_FORMATTER))
                .endTime(to.format(FinalValue.DAYTIME_FORMATTER)).build();
        HttpEntity request = new HttpEntity(dto,headers);
        ResponseEntity<ScheduleResponseDto> response = restTemplate.postForEntity("/schedule",request,ScheduleResponseDto.class);
        ScheduleResponseDto body = response.getBody();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(body.getId()).isNotNull();
        Assertions.assertThat(body.getCrop().getId()).isEqualTo(dto.getCropId());
        Assertions.assertThat(body.getToDo()).isEqualTo(dto.getToDo());
        Assertions.assertThat(body.getStartTime()).isEqualTo(dto.getStartTime());
        Assertions.assertThat(body.getEndTime()).isEqualTo(dto.getEndTime());

        // 일정 수정
        ScheduleRequestDto changeDto = ScheduleRequestDto.builder()
                .toDo("toDo Change")
                .cropId(2)
                .startTime(now.format(FinalValue.DAYTIME_FORMATTER))
                .endTime(now.format(FinalValue.DAYTIME_FORMATTER)).build();
        request = new HttpEntity(changeDto,headers);
        response = restTemplate.exchange("/schedule/"+body.getId(),HttpMethod.PUT,request,ScheduleResponseDto.class);
        body = response.getBody();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(body.getId()).isNotNull();
        Assertions.assertThat(body.getCrop().getId()).isEqualTo(changeDto.getCropId());
        Assertions.assertThat(body.getToDo()).isEqualTo(changeDto.getToDo());
        Assertions.assertThat(body.getStartTime()).isEqualTo(changeDto.getStartTime());
        Assertions.assertThat(body.getEndTime()).isEqualTo(changeDto.getEndTime());

        request = new HttpEntity(headers);
        // 일주일 일정 조회
        ResponseEntity<List<ScheduleResponseDto>> listResponse = restTemplate.exchange("/schedule", HttpMethod.GET, request, new ParameterizedTypeReference<List<ScheduleResponseDto>>() {
        });
        Assertions.assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 1달치 일정 조회
        listResponse = restTemplate.exchange("/schedule/"+now.getYear()+"-"+now.getMonthValue(), HttpMethod.GET, request, new ParameterizedTypeReference<List<ScheduleResponseDto>>() {
        });
        Assertions.assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 당일 일정 조회
        listResponse = restTemplate.exchange("/schedule/today", HttpMethod.GET, request, new ParameterizedTypeReference<List<ScheduleResponseDto>>() {
        });
        Assertions.assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        //일정 삭제
        response = restTemplate.exchange("/schedule/"+body.getId(),HttpMethod.DELETE,request,ScheduleResponseDto.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    void 작업일지_Controller() throws JsonProcessingException {
        //작업일지 생성
        LocalDate date = LocalDate.now();
        ObjectMapper mapper = new ObjectMapper();
        List<SubMaterialRequestDto> subs = new ArrayList<>();
        subs.add(SubMaterialRequestDto.builder().type(SubMaterialType.비료.ordinal())
                .product("test 비료")
                .use("100 포대").build());
        subs.add(SubMaterialRequestDto.builder().type(SubMaterialType.작물.ordinal())
                .product("test 작물 씨앗")
                .use("100 판").build());
        WorkLogRequestDto dto = WorkLogRequestDto.builder()
                .crop(1)
                .date(date.format(FinalValue.DAY_FORMATTER))
                .harvest(1000L)
                .subMaterial(subs)
                .title("test Title")
                .workTime(16)
                .memo("test Memo").build();
        MultiValueMap<String,Object> parameter = new LinkedMultiValueMap<>();
        parameter.add("data",mapper.writer().writeValueAsString(dto));

        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity request = new HttpEntity(parameter,headers);
        ResponseEntity<WorkLogResponseDto> response = restTemplate.postForEntity("/worklog",request,WorkLogResponseDto.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 작업일지 전체 조회
        headers.setContentType(MediaType.APPLICATION_JSON);
        request = new HttpEntity(headers);
        ResponseEntity<List<WorkLogResponseDto>> listResponse = restTemplate.exchange("/worklog", HttpMethod.GET, request, new ParameterizedTypeReference<List<WorkLogResponseDto>>() {
        });
        Assertions.assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        WorkLogResponseDto body = listResponse.getBody().get(0);
        Assertions.assertThat(body.getId()).isNotNull();
        Assertions.assertThat(body.getCrop().getId()).isEqualTo(dto.getCrop());
        Assertions.assertThat(body.getDate()).isEqualTo(dto.getDate());
        Assertions.assertThat(body.getMemo()).isEqualTo(dto.getMemo());
        Assertions.assertThat(body.getHarvest()).isEqualTo(dto.getHarvest());
        Assertions.assertThat(body.getSubMaterial().stream().filter(e->e.getProduct().equals("test 비료")).findFirst().orElse(null)).isNotNull();
        Assertions.assertThat(body.getSubMaterial().stream().filter(e->e.getProduct().equals("test 작물 씨앗")).findFirst().orElse(null)).isNotNull();
        Assertions.assertThat(body.getTitle()).isEqualTo(dto.getTitle());
        Assertions.assertThat(body.getWorkTime()).isEqualTo(dto.getWorkTime());

        // 작업일지 상세 조회
        response = restTemplate.exchange("/worklog/"+body.getId(),HttpMethod.GET,request, WorkLogResponseDto.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo(body.getTitle());
        Assertions.assertThat(response.getBody().getHarvest()).isEqualTo(body.getHarvest());
        Assertions.assertThat(response.getBody().getWorkTime()).isEqualTo(body.getWorkTime());
        Assertions.assertThat(response.getBody().getDate()).isEqualTo(body.getDate());
        Assertions.assertThat(response.getBody().getId()).isEqualTo(body.getId());
        Assertions.assertThat(response.getBody().getCrop().getId()).isEqualTo(body.getCrop().getId());
        Assertions.assertThat(response.getBody().getMemo()).isEqualTo(body.getMemo());
        Assertions.assertThat(response.getBody().getSubMaterial().stream().filter(e->e.getProduct().equals("test 비료")).findFirst().orElse(null)).isNotNull();
        Assertions.assertThat(response.getBody().getSubMaterial().stream().filter(e->e.getProduct().equals("test 작물 씨앗")).findFirst().orElse(null)).isNotNull();

        // 월별 수확량 통계
        ResponseEntity<LineChartDto> lineResponse = restTemplate.exchange("/totalharvest/month",HttpMethod.GET,request,LineChartDto.class);
        Assertions.assertThat(lineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 연도별 수확량 통계
        lineResponse = restTemplate.exchange("/totalharvest/year",HttpMethod.GET,request,LineChartDto.class);
        Assertions.assertThat(lineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 작업 시간 통계
        lineResponse = restTemplate.exchange("/worktime",HttpMethod.GET,request,LineChartDto.class);
        Assertions.assertThat(lineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 작업일지 삭제
        ResponseEntity<String> deleteResponse = restTemplate.exchange("/worklog/"+body.getId(),HttpMethod.DELETE,request,String.class);
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    }
}
