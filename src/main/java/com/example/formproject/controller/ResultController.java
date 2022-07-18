package com.example.formproject.controller;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.response.AccountResponseDto;
import com.example.formproject.dto.response.CircleChartDto;
import com.example.formproject.dto.response.LineChartDataDto;
import com.example.formproject.dto.response.LineChartDto;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.AccountBookService;
import com.example.formproject.service.WorkLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Result Api",description = "통계관련 API(백규현)")
public class ResultController {
    private final AccountBookService accountBookService;

    private final WorkLogService workLogService;
    private final Environment env;

    @GetMapping("/totalharvest")
    @Operation(summary = "전체 수확량(막대그래프,전체데이터)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = LineChartDto.class))) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    public LineChartDto getHarvestResult(@AuthenticationPrincipal MemberDetail detail){
        return workLogService.getHarvestData(detail.getMember());
    }

    @GetMapping("/sales")
    @Operation(summary = "판매,지출,순이익(막대그래프,전체데이터)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = LineChartDto.class))) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    public LineChartDto getAccountResult(@AuthenticationPrincipal MemberDetail detail){
        return accountBookService.getResults(detail.getMember());
    }

    @GetMapping("/income")
    @Operation(summary = "판매(Pie그래프,1년)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CircleChartDto.class))) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    public CircleChartDto getIncomeOfYear(@AuthenticationPrincipal MemberDetail detail){
        return accountBookService.getIncomeResult(detail.getMember());
    }

    @GetMapping("/expense")
    @Operation(summary = "지출(Pie그래프,1년)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CircleChartDto.class))) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    public CircleChartDto getExpenseOfYear(@AuthenticationPrincipal MemberDetail detail){
        return accountBookService.getExpenseResult(detail.getMember());
    }
    @GetMapping("/worktime")
    @Operation(summary = "분기별 작업 시간 데이터(막대그래프,작년,금년)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = LineChartDto.class))) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    public LineChartDto getWorkTimeResult(@AuthenticationPrincipal MemberDetail detail){
        return workLogService.getWorkTimeData(detail.getMember());
    }
    @GetMapping("/profile")
    @Operation(summary = "무중단 배포 확인용(무시해도됨)")
    public String getProfile () {
        return Arrays.stream(env.getActiveProfiles())
                .findFirst()
                .orElse("");
    }
}
