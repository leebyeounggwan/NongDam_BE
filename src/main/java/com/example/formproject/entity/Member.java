package com.example.formproject.entity;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.MemberInfoRequestDto;
import com.example.formproject.repository.CropRepository;
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
    @Builder.Default
    private String address="";

    @Column
    private int countryCode;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn
    private Images profileImage;
    @Column
    private String nickname;

    @Column(columnDefinition = "boolean default true")
    @Builder.Default
    private boolean isLock = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @Builder.Default
    private List<Crop> crops = new ArrayList<>();

    public void updateMember(OAuthAttributes attributes) {
        this.name = attributes.getName();
        this.profileImage = Images.builder().url(attributes.getPicture()).fileName("kakaoProfile").build();
    }

    public void updateMember(MemberInfoRequestDto requestDto, Map<String, String> profileImage, CropRepository repository) {
        this.nickname = requestDto.getNickname() == null ? nickname : requestDto.getNickname();
        this.address = requestDto.getAddress() == null ? address : requestDto.getAddress();
        this.countryCode = requestDto.getCountryCode() == 0 ? countryCode : requestDto.getCountryCode();
        this.profileImage = Images.builder().url(profileImage.get("url")).fileName(profileImage.get("fileName")).build();
        this.crops.clear();
        List<Crop> cr = repository.findAllIds(requestDto.getCrops());
        this.crops.addAll(cr);
    }

    public void updateMember(MemberInfoRequestDto requestDto, CropRepository repository) {
        // 프로필 사진 없이 업데이트
        this.nickname = requestDto.getNickname() == null ? nickname : requestDto.getNickname();
        this.address = requestDto.getAddress() == null ? address : requestDto.getAddress();
        this.countryCode = requestDto.getCountryCode() == 0 ? countryCode : requestDto.getCountryCode();
        this.profileImage = Images.builder().fileName("default").url(FinalValue.BACK_URL + "/static/default.png").build(); //기본 프로필 이미지
        this.crops.clear();
        List<Crop> cr = repository.findAllIds(requestDto.getCrops());
        this.crops.addAll(cr);
    }

    public void enableId() {
        if (isLock)
            this.isLock = false;
    }
}