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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    void ???????????????(){
        HttpEntity request = new HttpEntity(null,headers);
        ResponseEntity<String> response = restTemplate.exchange("/actuator/health",HttpMethod.GET,request,String.class);
        String body = response.getBody();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(body).contains("UP");
    }
    @Test
    @Order(1)
    void ????????????() {
        MemberRequestDto dto = MemberRequestDto.builder()
                .email("qweasd3996@naver.com")
                .name("?????????")
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
    void ??????????????????(){
        ResponseEntity response = restTemplate.getForEntity("/member/email?id=1",String.class);
        Assertions.assertThat(response.getStatusCodeValue()).isEqualTo(302);
    }
    @Test
    @Order(3)
    void ?????????(){
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
    void ???????????????_??????(){
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
    void ????????????_??????() throws JsonProcessingException {
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MemberInfoRequestDto dto = MemberInfoRequestDto.builder()
                .address("????????? ????????? ????????? ?????????")
                .countryCode(CountryCode.??????.getType())
                .nickname("????????? ?????????")
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
    void ?????????_????????????_??????() {
        HttpEntity request = new HttpEntity(headers);
        ResponseEntity<MemberResponseDto> response = restTemplate.exchange("/member", HttpMethod.GET, request, MemberResponseDto.class);
        MemberResponseDto dto = response.getBody();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(dto.getAddress()).isEqualTo("????????? ????????? ????????? ?????????");
        Assertions.assertThat(dto.getNickname()).isEqualTo("????????? ?????????");
        for(int i = 1; i < 4; i++) {
            int finalI = i;
            Assertions.assertThat(dto.getCrops().stream().filter(e->e.getId()== finalI).findFirst().orElse(null)).isNotNull();
        }
    }

    @Test
    void ??????_Controller(){
        // ?????? ?????? ??????
        LocalDate now = LocalDate.now();
        AccountRequestDto dto = AccountRequestDto.builder()
                .type(AccountType.???????????????.ordinal())
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
                .type(AccountType.?????????_??????.ordinal())
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
        // ?????? ?????? ??????
        dto = AccountRequestDto.builder()
                .type(AccountType.???????????????.ordinal())
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

        // ?????? ?????? ??????
        request = new HttpEntity(headers);
        ResponseEntity<List<AccountResponseDto>> listResponse = restTemplate.exchange("/accountbook/" + now.getYear() + "-" + now.getMonthValue(),HttpMethod.GET,request, new ParameterizedTypeReference<List<AccountResponseDto>>() {});
        Assertions.assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // ???????????? ?????? ??????
//        request = new HttpEntity(headers);
        listResponse = restTemplate.exchange("/accountbook" ,HttpMethod.GET,request, new ParameterizedTypeReference<List<AccountResponseDto>>() {});
        Assertions.assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // ?????? ??????
        ResponseEntity<CircleChartDto> circleResponse = restTemplate.exchange("/income",HttpMethod.GET,request,CircleChartDto.class);
        Assertions.assertThat(circleResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ????????????
        circleResponse = restTemplate.exchange("/expense",HttpMethod.GET,request,CircleChartDto.class);
        Assertions.assertThat(circleResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ????????? ??????/?????? ??????
        ResponseEntity<LineChartDto> lineResponse = restTemplate.exchange("/sales/year",HttpMethod.GET,request,LineChartDto.class);
        Assertions.assertThat(lineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ?????? ??????/?????? ??????
        lineResponse = restTemplate.exchange("/sales/month",HttpMethod.GET,request,LineChartDto.class);
        Assertions.assertThat(lineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // ?????? ??????
//        request = new HttpEntity(headers);
        ResponseEntity<String> deleteResponse = restTemplate.exchange("/accountbook/"+originalDto.getId(),HttpMethod.DELETE,request,String.class);
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        deleteResponse = restTemplate.exchange("/accountbook/"+originalDto2.getId(),HttpMethod.DELETE,request,String.class);
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void ????????????_Controller(){
        HttpEntity request = new HttpEntity(headers);
        // ?????? ????????????
        ResponseEntity<List<CropDto>> response = restTemplate.exchange("/crops", HttpMethod.GET, request, new ParameterizedTypeReference<List<CropDto>>() {
        });
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ?????? ?????? ??????
        response = restTemplate.exchange("/crop",HttpMethod.GET,request, new ParameterizedTypeReference<List<CropDto>>() {
        });
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ?????? API
        ResponseEntity<List<NewsResponseDto>> newsResponse = restTemplate.exchange("/news", HttpMethod.GET, request, new ParameterizedTypeReference<List<NewsResponseDto>>() {
        });
        Assertions.assertThat(newsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    void ??????_Controller(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime to = now.plusHours(1L);
        // ?????? ??????
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

        // ?????? ??????
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
        // ????????? ?????? ??????
        ResponseEntity<List<ScheduleResponseDto>> listResponse = restTemplate.exchange("/schedule", HttpMethod.GET, request, new ParameterizedTypeReference<List<ScheduleResponseDto>>() {
        });
        Assertions.assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // 1?????? ?????? ??????
        listResponse = restTemplate.exchange("/schedule/"+now.getYear()+"-"+now.getMonthValue(), HttpMethod.GET, request, new ParameterizedTypeReference<List<ScheduleResponseDto>>() {
        });
        Assertions.assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ?????? ?????? ??????
        listResponse = restTemplate.exchange("/schedule/today", HttpMethod.GET, request, new ParameterizedTypeReference<List<ScheduleResponseDto>>() {
        });
        Assertions.assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        //?????? ??????
        response = restTemplate.exchange("/schedule/"+body.getId(),HttpMethod.DELETE,request,ScheduleResponseDto.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
    @Test
    void ????????????_Controller() throws JsonProcessingException {
        //???????????? ??????
        LocalDate date = LocalDate.now();
        ObjectMapper mapper = new ObjectMapper();
        List<SubMaterialRequestDto> subs = new ArrayList<>();
        subs.add(SubMaterialRequestDto.builder().type(SubMaterialType.??????.ordinal())
                .product("test ??????")
                .use("100 ??????").build());
        subs.add(SubMaterialRequestDto.builder().type(SubMaterialType.??????.ordinal())
                .product("test ?????? ??????")
                .use("100 ???").build());
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

        // ???????????? ?????? ??????
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
        Assertions.assertThat(body.getSubMaterial().stream().filter(e->e.getProduct().equals("test ??????")).findFirst().orElse(null)).isNotNull();
        Assertions.assertThat(body.getSubMaterial().stream().filter(e->e.getProduct().equals("test ?????? ??????")).findFirst().orElse(null)).isNotNull();
        Assertions.assertThat(body.getTitle()).isEqualTo(dto.getTitle());
        Assertions.assertThat(body.getWorkTime()).isEqualTo(dto.getWorkTime());

        // ???????????? ?????? ??????
        response = restTemplate.exchange("/worklog/"+body.getId(),HttpMethod.GET,request, WorkLogResponseDto.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody().getTitle()).isEqualTo(body.getTitle());
        Assertions.assertThat(response.getBody().getHarvest()).isEqualTo(body.getHarvest());
        Assertions.assertThat(response.getBody().getWorkTime()).isEqualTo(body.getWorkTime());
        Assertions.assertThat(response.getBody().getDate()).isEqualTo(body.getDate());
        Assertions.assertThat(response.getBody().getId()).isEqualTo(body.getId());
        Assertions.assertThat(response.getBody().getCrop().getId()).isEqualTo(body.getCrop().getId());
        Assertions.assertThat(response.getBody().getMemo()).isEqualTo(body.getMemo());
        Assertions.assertThat(response.getBody().getSubMaterial().stream().filter(e->e.getProduct().equals("test ??????")).findFirst().orElse(null)).isNotNull();
        Assertions.assertThat(response.getBody().getSubMaterial().stream().filter(e->e.getProduct().equals("test ?????? ??????")).findFirst().orElse(null)).isNotNull();

        // ?????? ????????? ??????
        ResponseEntity<LineChartDto> lineResponse = restTemplate.exchange("/totalharvest/month",HttpMethod.GET,request,LineChartDto.class);
        Assertions.assertThat(lineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ????????? ????????? ??????
        lineResponse = restTemplate.exchange("/totalharvest/year",HttpMethod.GET,request,LineChartDto.class);
        Assertions.assertThat(lineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ?????? ?????? ??????
        lineResponse = restTemplate.exchange("/worktime",HttpMethod.GET,request,LineChartDto.class);
        Assertions.assertThat(lineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        // ???????????? ??????
        ResponseEntity<String> deleteResponse = restTemplate.exchange("/worklog/"+body.getId(),HttpMethod.DELETE,request,String.class);
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    }

    @Test
    void Http?????????() throws IOException {
        String link = "http://www.ddanzi.com/744864957";
        URL url = new URL(link);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String tmp;
        StringBuilder builder = new StringBuilder();
        while((tmp = reader.readLine()) != null){
            builder.append(tmp);
        }

    }
}
