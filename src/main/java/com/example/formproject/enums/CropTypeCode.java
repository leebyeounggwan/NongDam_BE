package com.example.formproject.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.stream.Collectors;

@Getter
public enum CropTypeCode {
    쌀(111),찹쌀(112),콩(141),팥(142),녹두(143),메밀(144),고구마(151),감자(152),
    배추(211),양배추(212),시금치(213),상추(214),얼갈이배추(215),갓(216),수박(221),참외(222),오이(223),호박(224),토마토(225),딸기(226),무(231),당근(232),열무(233),건고추(241),풋고추(242),붉은고추(243),피마늘(244),양파(245),파(246),생강(247),고춧가루(248),가지(251),미나리(252),깻잎(253),부추(254),피망(255),파프리카(256),멜론(257),깐마늘_국산(258),깐마늘_수입(259),
    사과(411),배(412),복숭아(413),포도(414),감귤(415),단감(416),바나나(418),참다래(419),파인애플(420),오렌지(421),방울토마토(422),자몽(423),레몬(424),체리(425),건포도(426),건블루베리(427),망고(428);
    private int type;
    CropTypeCode(int type){
        this.type = type;
    }

    public static CropTypeCode findByCode(int code){
        return Arrays.stream(values()).filter(e->e.getType() == code).collect(Collectors.toList()).get(0);
    }
}
