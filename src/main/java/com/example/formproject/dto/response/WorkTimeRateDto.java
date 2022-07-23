package com.example.formproject.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Getter
public class WorkTimeRateDto {
    private int rate;

    private String rateText;
}
