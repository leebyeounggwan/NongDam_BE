package com.example.formproject.controller;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.PriceInfoRequestDto;
import com.example.formproject.dto.request.PriceRequestDto;
import com.example.formproject.dto.response.DailyPriceResponseDto;
import com.example.formproject.dto.response.PriceInfoDto;
import com.example.formproject.entity.Crop;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.PriceInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
    @GetMapping("/todaymarketprice/{cropId}/{productClsCode}")
    @Operation(summary = "당일 시세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DailyPriceResponseDto.class)) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content = @Content)})
    @Parameter(in = ParameterIn.PATH,name = "cropId",description = "작물 정보",example = "21",required = true)
    @Parameter(in = ParameterIn.PATH,name = "productClsCode",description = "도/소매",example = "소매",required = true)
    public DailyPriceResponseDto todayPriceInfo(@PathVariable("cropId")int cropId, @PathVariable("productClsCode")String productClsCode,
                                                @AuthenticationPrincipal MemberDetail memberdetail) throws IOException, ParseException {
        PriceRequestDto priceRequestDto = new PriceRequestDto();
        priceRequestDto.setCropId(cropId);
        priceRequestDto.setProductClsCode(productClsCode);
        Crop crop = cropRepository.findById(priceRequestDto.getCropId()).orElseThrow();

        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceRequestDto, memberdetail);
        System.out.println(memberdetail.getMember().getCountryCode());
        return priceInfoService.dailyPrice(priceInfoRequestDto);
    }
    @GetMapping("/marketprice")
    @Operation(summary = "월별/연도별 시세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PriceInfoDto.class)) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content = @Content)})
    @Parameter(in = ParameterIn.PATH,name = "cropId",description = "작물 정보",example = "21",required = true)
    @Parameter(in = ParameterIn.PATH,name = "data",description = "월별/연도별 선택",example = "month",required = true)
    public List<PriceInfoDto> priceInfo(@RequestParam int cropId, @RequestParam String data,
                                        @AuthenticationPrincipal MemberDetail memberdetail) throws IOException, ParseException {
        PriceRequestDto priceRequestDto = new PriceRequestDto();
        priceRequestDto.setCropId(cropId);
        priceRequestDto.setData(data);
        Crop crop = cropRepository.findById(priceRequestDto.getCropId()).orElseThrow();

        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceRequestDto, memberdetail);
        List<PriceInfoDto> reponseDto = (priceInfoRequestDto.getData().equals("month")) ? priceInfoService.monthlyPrice(priceInfoRequestDto) : priceInfoService.yearlyPrice(priceInfoRequestDto);

        return reponseDto;
    }

    @GetMapping("/marketprice/{cropId}/{data}")
    @Operation(summary = "내가 등록한 작물의 월별/연도별 시세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PriceInfoDto.class)) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content = @Content)})
    @Parameter(in = ParameterIn.PATH,name = "cropId",description = "작물 정보",example = "21",required = true)
    @Parameter(in = ParameterIn.PATH,name = "data",description = "월별/연도별 선택",example = "month",required = true)
    public List<PriceInfoDto> myPriceInfo(@PathVariable("cropId")int cropId, @PathVariable("data")String data,
                                        @AuthenticationPrincipal MemberDetail memberdetail) throws IOException, ParseException {
        PriceRequestDto priceRequestDto = new PriceRequestDto();
        priceRequestDto.setCropId(cropId);
        priceRequestDto.setData(data);
        Crop crop = cropRepository.findById(priceRequestDto.getCropId()).orElseThrow();

        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceRequestDto, memberdetail);
        List<PriceInfoDto> reponseDto = (priceInfoRequestDto.getData().equals("month")) ? priceInfoService.monthlyPrice(priceInfoRequestDto) : priceInfoService.yearlyPrice(priceInfoRequestDto);

        return reponseDto;
    }
}
