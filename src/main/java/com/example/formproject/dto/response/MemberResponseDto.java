package com.example.formproject.dto.response;

import com.example.formproject.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {
    @Schema(type = "PK", example = "1")
    private int id;
    @Schema(type = "String", example = "example name")
    private String name;
    @Schema(type = "String", example = "example nickname")
    private String nickname;
    @Schema(type = "email", example = "example@abcd.com")
    private String email;
    @Schema(type = "String", example = "example address")
    private String address;
    @Schema(type = "String", example = "example profile Image Url")
    private String profileImage;
    @Schema(type = "int", example = "1101")
    private int countryCode;
    private List<CropDto> crops = new ArrayList<>();

    public MemberResponseDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.address = member.getAddress();
        this.nickname = member.getNickname();
        this.profileImage = member.getProfileImage();
        this.countryCode = member.getCountryCode();
        member.getCrops().stream().forEach(e -> this.crops.add(new CropDto(e)));
    }
}
