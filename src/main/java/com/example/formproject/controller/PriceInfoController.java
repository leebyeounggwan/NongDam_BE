package com.example.formproject.controller;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.PriceInfoRequestDto;
import com.example.formproject.dto.request.PriceInfoRequestDto2;
import com.example.formproject.dto.response.DailyPriceInfoDto;
import com.example.formproject.dto.response.PriceInfoDto;
import com.example.formproject.dto.response.ScheduleResponseDto;
import com.example.formproject.entity.Crop;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.PriceInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PriceInfoController {

    private final PriceInfoService priceInfoService;
    private final CropRepository cropRepository;
    @PostMapping("/todaymarketprice")
    @Operation(summary = "당일 시세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleResponseDto.class)) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content = @Content)})
    public DailyPriceInfoDto todayPriceInfo(@RequestBody PriceInfoRequestDto2 priceInfoRequestDto2,
                                            @AuthenticationPrincipal MemberDetail memberdetail) throws IOException, ParseException {

        Crop crop = cropRepository.findById(priceInfoRequestDto2.getCropId()).orElseThrow();
        System.out.println(crop.getCategory());
        System.out.println(crop.getType());
        System.out.println(crop.getKind());
        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceInfoRequestDto2, memberdetail);
        System.out.println(memberdetail.getMember().getCountryCode());
        return priceInfoService.dailyPrice(priceInfoRequestDto);
    }
    @PostMapping("/marketprice")
    @Operation(summary = "월별/연도별 시세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleResponseDto.class)) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content = @Content)})

    public List<PriceInfoDto> priceInfo(@RequestBody PriceInfoRequestDto2 priceInfoRequestDto2,
                                        @AuthenticationPrincipal MemberDetail memberdetail) throws IOException, ParseException {
        Crop crop = cropRepository.findById(priceInfoRequestDto2.getCropId()).orElseThrow();
        System.out.println(crop.getCategory());
        System.out.println(crop.getType());
        System.out.println(crop.getKind());

        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceInfoRequestDto2, memberdetail);
        List<PriceInfoDto> reponseDto = (priceInfoRequestDto.getData().equals("month")) ? priceInfoService.monthlyPrice(priceInfoRequestDto) : priceInfoService.yearlyPrice(priceInfoRequestDto);

        return reponseDto;
    }
}
