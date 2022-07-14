package com.example.formproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WeatherDto {
    // 일간/주간 시간
    @Schema(type = "List<Long>",example = "[1656993600, 1656993600, 1656993600, 1656993600, 1656993600, 1656993600]")
    private List<Long> time;
    // 일간/주간 기온
    @Schema(type = "List<String>",example = "[23, 22, 23, 24, 23, 21]")
    private List<String> temp;
    // 일간/주간 강수확률
    @Schema(type = "List<String>",example = "[12, 0, 100, 70, 45, 20]")
    private List<String> pop;
}
