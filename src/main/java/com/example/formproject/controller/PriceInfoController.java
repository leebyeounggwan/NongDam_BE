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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.sql.PreparedStatement;
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
        PriceRequestDto priceRequestDto = new PriceRequestDto().dailyPriceRequestDto(cropId, productClsCode);

        Crop crop = cropRepository.findById(priceRequestDto.getCropId()).orElseThrow(() -> new NullPointerException("해당 작물이 없습니다."));
        String type = productClsCode;
        String country = memberdetail.getMember().getCountryCode()+"";
        String countryCode = (country.equals("0")) ? "1101" : country;
        String cacheKey = cropId + countryCode + type;
        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceRequestDto, memberdetail);
        return priceInfoService.dailyPrice(priceInfoRequestDto, cacheKey);
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
        PriceRequestDto priceRequestDto = new PriceRequestDto(cropId, data);
        Crop crop = cropRepository.findById(priceRequestDto.getCropId()).orElseThrow(() -> new NullPointerException("해당 작물이 없습니다."));
        String type = data;
        String country = memberdetail.getMember().getCountryCode()+"";
        String countryCode = (country.equals("0")) ? "1101" : country;
        String cacheKey = cropId + countryCode + type;

        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceRequestDto, memberdetail);
        List<PriceInfoDto> reponseDto = (priceInfoRequestDto.getData().equals("month")) ? priceInfoService.monthlyPrice(priceInfoRequestDto, cacheKey) : priceInfoService.yearlyPrice(priceInfoRequestDto, cacheKey);

        return reponseDto;
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
        String type = data;
        String country = memberdetail.getMember().getCountryCode()+"";
        String countryCode = (country.equals("0")) ? "1101" : country;

        if (crops.size() != 0) {
            for (Crop crop : crops) {
                int cropId = crop.getId();
                String cacheKey = cropId + countryCode + type;
                PriceRequestDto priceRequestDto = new PriceRequestDto(crop.getId(), data);
                PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceRequestDto, memberdetail);
                List<PriceInfoDto> reponseDto = (priceInfoRequestDto.getData().equals("month")) ? priceInfoService.monthlyPrice(priceInfoRequestDto, cacheKey) : priceInfoService.yearlyPrice(priceInfoRequestDto, cacheKey);
                responseDtoList.add(reponseDto);
            }
        }
        return responseDtoList;
    }

/*    @GetMapping("/marketprices/year")
    @Operation(summary = "내가 등록한 모든 작물의 연도별 시세 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PriceInfoDto.class)) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content = @Content)})
    public List<List<PriceInfoDto>> myPriceInfoYear(@AuthenticationPrincipal MemberDetail memberdetail) throws ParseException {


        List<List<PriceInfoDto>> responseDtoList = new ArrayList<>();
        List<Crop> crops = memberdetail.getMember().getCrops();
        String type = "year";
        String country = memberdetail.getMember().getCountryCode()+"";
        String countryCode = (country.equals("0")) ? "1101" : country;

        if (crops.size() != 0) {
            for (Crop crop : crops) {
                int cropId = crop.getId();
                String cacheKey = cropId + countryCode + type;
                PriceRequestDto priceRequestDto = new PriceRequestDto(crop.getId(), "year");
                System.out.println(crop.getType());
                System.out.println(crop.getKind());
                PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceRequestDto, memberdetail);
                List<PriceInfoDto> reponseDto = priceInfoService.yearlyPrice(priceInfoRequestDto, cacheKey);
                responseDtoList.add(reponseDto);
            }
        }
        return responseDtoList;
    }*/
}
