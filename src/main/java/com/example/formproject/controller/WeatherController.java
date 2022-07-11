package com.example.formproject.controller;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.response.AccountResponseDto;
import com.example.formproject.dto.response.ScheduleResponseDto;
import com.example.formproject.dto.response.WeatherResponse;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.GeoService;
import com.example.formproject.service.OpenWeatherApiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Weather Api",description = "날씨 정보 API (이병관)")
public class WeatherController {

    private final OpenWeatherApiService weatherService;

    @GetMapping("/weather")
    @Operation(summary = "날씨 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleResponseDto.class)) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content = @Content)})
    public WeatherResponse getWeather(@AuthenticationPrincipal MemberDetail memberdetail) throws IOException, ParseException {
        return weatherService.getWeather(memberdetail,memberdetail.getMember().getId());
    }

}
