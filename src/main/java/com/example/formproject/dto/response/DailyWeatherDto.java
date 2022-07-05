package com.example.formproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DailyWeatherDto {
    private List<Long> day;
    private List<String> temp;
    private List<String> pop;
}
