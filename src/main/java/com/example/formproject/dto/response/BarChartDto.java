package com.example.formproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

public class BarChartDto {
    @Builder.Default
    @Schema(type = "List",example = "[\"2021-06\",\"2022-07\"]")
    private List<String> xLabel = new ArrayList<>();

    @Builder.Default
    private List<LineChartDataDto> datas = new ArrayList<>();
}
