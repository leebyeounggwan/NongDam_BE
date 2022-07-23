package com.example.formproject.entity;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.MemberInfoRequestDto;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.security.MemberDetail;
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
    private String address = "";

    @Column
    private int countryCode;

    @Column
    private String profileImage;
    @Column
    private String nickname;

    @Column(columnDefinition = "boolean default true")
    @Builder.Default
    private boolean isLock = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private List<Crop> crops = new ArrayList<>();

    public void changePassword(String password){
        this.password = password;
    }
    public void updateMember(OAuthAttributes attributes) {
        this.name = attributes.getName();
        this.profileImage = attributes.getPicture();
    }

    public void updateMember(MemberInfoRequestDto requestDto, Map<String, String> profileImage, CropRepository repository) {
        this.nickname = requestDto.getNickname() == null ? nickname : requestDto.getNickname();
        this.address = requestDto.getAddress() == null ? address : requestDto.getAddress();
        this.countryCode = requestDto.getCountryCode() == 0 ? countryCode : requestDto.getCountryCode();
        this.profileImage = profileImage.get("url");
        this.crops.clear();
        List<Crop> cr = repository.findAllIds(requestDto.getCrops());
        this.crops.addAll(cr);
    }

    public void updateMember(MemberInfoRequestDto requestDto, String defaultImage, CropRepository repository) {
        // 프로필 사진 없이 업데이트
        this.nickname = requestDto.getNickname() == null ? nickname : requestDto.getNickname();
        this.address = requestDto.getAddress() == null ? address : requestDto.getAddress();
        this.countryCode = requestDto.getCountryCode() == 0 ? countryCode : requestDto.getCountryCode();
        this.profileImage = defaultImage; //기존 이미지
        this.crops.clear();
        List<Crop> cr = repository.findAllIds(requestDto.getCrops());
        this.crops.addAll(cr);
    }

    public void enableId() {
        if (isLock)
            this.isLock = false;
    }
}