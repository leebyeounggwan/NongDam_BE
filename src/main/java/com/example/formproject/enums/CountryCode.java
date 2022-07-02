package com.example.formproject.enums;

import lombok.Getter;

@Getter
public enum CountryCode {
    서울(1101),부산(2101),대구(2202),인천(2300),광주(2401),대전(2501),울산(2601),수원(3111),춘천(3211),
    청주(3311),전주(3511),포항(3711),제주(3911),의정부(3113),순천(3613),안동(3714),창원(3814),용인(3145);
    private int type;
    CountryCode(int type){
        this.type = type;
    }
}
