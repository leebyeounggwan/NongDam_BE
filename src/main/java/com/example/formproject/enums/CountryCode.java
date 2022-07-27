package com.example.formproject.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public enum CountryCode {
    서울(1101),부산(2100),대구(2200),인천(2300),광주(2401),대전(2501),울산(2601),수원(3111),춘천(3211),
    청주(3311),전주(3511),포항(3711),제주(3911),의정부(3113),순천(3613),안동(3714),창원(3814),용인(3145);
    private int type;
    CountryCode(int type){
        this.type = type;
    }

    public static CountryCode findByCountryCode(int code){
        return Arrays.stream(values()).filter(e->e.getType() == code).findFirst().orElse(CountryCode.서울);
    }
}
