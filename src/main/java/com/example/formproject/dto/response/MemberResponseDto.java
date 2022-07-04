package com.example.formproject.dto.response;

import com.example.formproject.entity.Member;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class MemberResponseDto {
    private int id;
    private String name;
    private String nickname;
    private String email;
    private String address;
    private String profileImage;
    private int countryCode;
    private List<CropDto> crops = new ArrayList<>();
    public MemberResponseDto(Member member){
        this.id = member.getId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.address= member.getAddress();
        this.nickname = member.getNickname();
        this.profileImage = member.getProfileImage();
        this.countryCode = member.getCountryCode();
        member.getCrops().stream().forEach(e->this.crops.add(new CropDto(e)));
    }
}
