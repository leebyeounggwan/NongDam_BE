package com.example.formproject.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DailyWeatherDto {
    private String day;
    private String temp;
    private String pop;
}