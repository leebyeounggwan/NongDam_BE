package com.example.formproject.dto.request;

import com.example.formproject.entity.Crop;
import lombok.Getter;

import java.util.List;

@Getter
public class MemberInfoRequestDto {
    private String nickname;
    private String address;
    private int contryCode;
    private List<Crop> crops;
    private String profileImage;
}