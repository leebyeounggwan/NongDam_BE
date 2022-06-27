package com.example.formproject.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

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

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private int contryCode;

    @Column
    private String profileImage;

    @Column
    private String nickname;

    @OneToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private List<Crop> crops = new ArrayList<>();

}
