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
                                                @AuthenticationPrincipal MemberDetail memberdetail) throws IOException, ParseException {
        PriceRequestDto priceRequestDto = new PriceRequestDto().dailyPriceRequestDto(cropId, productClsCode);
        Crop crop = cropRepository.findById(priceRequestDto.getCropId()).orElseThrow();
        System.out.println(crop.getType());
        System.out.println(crop.getKind());
        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceRequestDto, memberdetail);
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
        PriceRequestDto priceRequestDto = new PriceRequestDto(cropId, data);
        Crop crop = cropRepository.findById(priceRequestDto.getCropId()).orElseThrow();
        System.out.println(crop.getType());
        System.out.println(crop.getKind());
        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceRequestDto, memberdetail);
        List<PriceInfoDto> reponseDto = (priceInfoRequestDto.getData().equals("month")) ? priceInfoService.monthlyPrice(priceInfoRequestDto) : priceInfoService.yearlyPrice(priceInfoRequestDto);

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
    @Parameter(in = ParameterIn.PATH,name = "cropId",description = "작물 정보",example = "21",required = true)
    @Parameter(in = ParameterIn.PATH,name = "data",description = "월별/연도별 선택",example = "month",required = true)
    public List<List<PriceInfoDto>> myPriceInfo(@PathVariable("data") String data,
                                                @AuthenticationPrincipal MemberDetail memberdetail) throws IOException, ParseException {

        List<List<PriceInfoDto>> responseDtoList = new ArrayList<>();
        List<Crop> crops = memberdetail.getMember().getCrops();

        if (crops.size() != 0) {
            for (Crop crop : crops) {
                PriceRequestDto priceRequestDto = new PriceRequestDto(crop.getId(), data);
                System.out.println(crop.getType());
                System.out.println(crop.getKind());
                PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceRequestDto, memberdetail);
                List<PriceInfoDto> reponseDto = (priceInfoRequestDto.getData().equals("month")) ? priceInfoService.monthlyPrice(priceInfoRequestDto) : priceInfoService.yearlyPrice(priceInfoRequestDto);
                responseDtoList.add(reponseDto);
            }
        }
        return responseDtoList;
    }
}
