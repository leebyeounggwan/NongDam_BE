package com.example.formproject.controller;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.response.*;
import com.example.formproject.entity.Crop;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.CropService;
import com.example.formproject.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Common Info Api",description = "기준정보 관련 API(이경동)")
@Slf4j
public class CommonInfoController {
    private final CropService cropService;
    private final NewsService newsService;
    @GetMapping("/crops")
    @Operation(summary = "전체 작물 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CropDto.class)))
            }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)
    })
    public List<CropDto> getAllCrops(){
        log.info("testLog");
        return cropService.findAllData();
    }

    @GetMapping("/crop")
    @Operation(summary = "개인 작물 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CropDto.class))) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content = @Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    public List<PersonalCropDto> getPersonalCrop(@AuthenticationPrincipal MemberDetail memberdetail) {
        return cropService.getPersonalCrop(memberdetail);
    }

    @GetMapping("/news")
    @Operation(summary = "뉴스 조회")
    public List<NewsResponseDto> getNews() throws IOException, ParseException, org.json.simple.parser.ParseException {
        List<NewsResponseDto> ret = newsService.getNewsInfo("");
        for(NewsResponseDto r: ret)
            r.setTime();
        return ret;
    }

}
