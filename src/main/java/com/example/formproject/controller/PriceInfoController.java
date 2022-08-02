package com.example.formproject.controller;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.PriceInfoRequestDto;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Validated
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
    public DailyPriceResponseDto todayPriceInfo(@PathVariable("cropId") int cropId, @PathVariable("productClsCode")String productClsCode,
                                                @AuthenticationPrincipal MemberDetail memberdetail) throws ParseException {
        Crop crop = cropRepository.findById(cropId).orElseThrow(() -> new NullPointerException("해당 작물이 없습니다."));
        CacheKey cacheKey = new CacheKey(memberdetail, productClsCode, crop);
        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, memberdetail, productClsCode);

        return priceInfoService.dailyPrice(priceInfoRequestDto, cacheKey.key);
    }

    @GetMapping("/marketprice")
    @Operation(summary = "선택한 작물의 월별/연도별 시세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PriceInfoDto.class)) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content = @Content)})
    @Parameter(in = ParameterIn.PATH,name = "cropId",description = "작물 정보",example = "21",required = true)
    @Parameter(in = ParameterIn.PATH,name = "data",description = "월별/연도별 선택",example = "month",required = true)
    public List<PriceInfoDto> priceInfo(@RequestParam int cropId, @RequestParam String data,
                                        @AuthenticationPrincipal MemberDetail memberdetail) throws ParseException {
        Crop crop = cropRepository.findById(cropId).orElseThrow(() -> new NullPointerException("해당 작물이 없습니다."));
        CacheKey cacheKey = new CacheKey(memberdetail, data, crop);
        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, data, memberdetail);

        return priceInfoService.monthAndYearPrice(priceInfoRequestDto, cacheKey.key);
    }

    @GetMapping("/marketprices/{data}")
    @Operation(summary = "내가 등록한 모든 작물의 월별/연도별 시세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PriceInfoDto.class)) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content = @Content)})
    @Parameter(in = ParameterIn.PATH,name = "data",description = "월별/연도별 선택",example = "month",required = true)
    public List<List<PriceInfoDto>> myPriceInfoMonth(
            @PathVariable String data,
            @AuthenticationPrincipal MemberDetail memberdetail) throws ParseException {

        List<List<PriceInfoDto>> responseDtoList = new ArrayList<>();
        List<Crop> crops = memberdetail.getMember().getCrops();

        if (crops.size() != 0) {
            for (Crop crop : crops) {
                CacheKey cacheKey = new CacheKey(memberdetail, data, crop);
                PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, data, memberdetail);
                List<PriceInfoDto> reponseDto = priceInfoService.monthAndYearPrice(priceInfoRequestDto, cacheKey.key);

                responseDtoList.add(reponseDto);
            }
        }
        return responseDtoList;
    }

    private static class CacheKey {
        String type;
        String country;
        String countryCode;
        int cropId;
        String key;

        private CacheKey(MemberDetail memberdetail, String data, Crop crop) {
            this.type = data;
            this.country = memberdetail.getMember().getCountryCode()+"";
            this.countryCode = (country.equals("0")) ? "1101" : country;
            this.cropId = crop.getId();
            this.key = cropId + countryCode + type;
        }
    }

}
