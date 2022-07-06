package com.example.formproject.controller;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.ScheduleRequestDto;
import com.example.formproject.dto.response.AccountResponseDto;
import com.example.formproject.dto.response.ScheduleResponseDto;
import com.example.formproject.entity.Member;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name="Schedule Api",description = "일정 관련 API(백규현)")
public class ScheduleController {
    private final ScheduleService scheduleService;
    @GetMapping("/schedule")
    @Operation(summary = "최근 일주일 일정 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ScheduleResponseDto.class))) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content = @Content)})
    public List<ScheduleResponseDto> getSchedules(@AuthenticationPrincipal MemberDetail detail){
        return scheduleService.findScheduleOfWeek(detail.getMember());
    }
    @GetMapping("/schedule/{yearMonth}")
    @Operation(summary = "1달치 일정조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ScheduleResponseDto.class))) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_BADREQUEST, description = "요청데이터 오류",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content = @Content)})
    @Parameter(in = ParameterIn.PATH,name = "yearMonth",description = "년월 정보",example = "2022-07",required = true)
    public List<ScheduleResponseDto> getMonthSchedules(@AuthenticationPrincipal MemberDetail detail,@PathVariable String yearMonth){
        String[] tmp= yearMonth.split("-");
        return scheduleService.findScheduleOfMonth(detail.getMember(),Integer.parseInt(tmp[0]),Integer.parseInt(tmp[1]));
    }
    @GetMapping("/schedule/today")
    @Operation(summary = "당일 일정 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ScheduleResponseDto.class))) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    public List<ScheduleResponseDto> getDaySchedules(@AuthenticationPrincipal MemberDetail detail){
        return scheduleService.findScheduleOfDay(detail.getMember());
    }

    @PostMapping("/schedule")
    @Operation(summary = "일정 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                           schema = @Schema(implementation = ScheduleResponseDto.class)) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_BADREQUEST, description = "요청데이터 오류",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content) })
    public ScheduleResponseDto saveSchedule(@RequestBody ScheduleRequestDto dto, @AuthenticationPrincipal MemberDetail detail){
        return scheduleService.save(detail.getMember(),dto);
    }

    @PutMapping("/schedule/{scheduleId}")
    @Operation(summary = "일정 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ScheduleResponseDto.class)) }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_BADREQUEST, description = "요청데이터 오류",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content) })
    @Parameter(in = ParameterIn.PATH,name = "scheduleId",description = "일정 id",example = "1",required = true)
    public ScheduleResponseDto editSchedule(@PathVariable Long scheduleId,@RequestBody ScheduleRequestDto dto){
        return scheduleService.save(scheduleId,dto);
    }
    @DeleteMapping("/schedule/{scheduleId}")
    @Operation(summary = "일정 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_OK, description = "응답 완료",
                    content = { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_FORBIDDEN, description = "로그인 필요",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_BADREQUEST, description = "요청데이터 오류",content=@Content),
            @ApiResponse(responseCode = FinalValue.HTTPSTATUS_SERVERERROR, description = "서버 오류",content=@Content)})
    @Parameter(in = ParameterIn.PATH,name = "scheduleId",description = "일정 id",example = "1",required = true)
    public void deleteSchedule(@PathVariable Long scheduleId){
        scheduleService.getScheduleRepository().deleteById(scheduleId);
    }
    @ExceptionHandler({NumberFormatException.class,IndexOutOfBoundsException.class})
    public ResponseEntity<String> badRequest(){
        return new ResponseEntity("요청데이터가 잘못되었습니다.", HttpStatus.BAD_REQUEST);
    }
}
