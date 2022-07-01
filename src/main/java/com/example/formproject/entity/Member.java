package com.example.formproject.entity;

import com.example.formproject.dto.request.MemberInfoRequestDto;
import com.example.formproject.security.OAuthAttributes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private String address;

    @Column
    private int countryCode;

    @Column
    private String profileImage;

    @Column
    private String nickname;

    @OneToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private List<Crop> crops = new ArrayList<>();

    public void updateMember(OAuthAttributes attributes){
        this.name = attributes.getName();
        this.profileImage = attributes.getPicture();
    }

    public void updateMember(MemberInfoRequestDto requestDto, Map<String, String> profileImage){
        this.nickname = requestDto.getNickname();
        this.address = requestDto.getAddress();
        this.countryCode = requestDto.getCountryCode();
        this.crops = requestDto.getCrops();
        this.profileImage = profileImage.get("url");
    }
}