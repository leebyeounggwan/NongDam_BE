package com.example.formproject.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class WorkLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @Column
    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Crop crop;

    @Column
    private String memo;

    @OneToMany(fetch = FetchType.LAZY)
    @Builder.Default
    private List<SubMaterial> subMaterials = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "worklog_pictures", joinColumns = {@JoinColumn(name = "work_log_id", referencedColumnName = "id")})
    @Column
    @Builder.Default
    private List<String> pictures = new ArrayList<>();

    @Column
    @Builder.Default

    private long harvest = 0L;

    @Column
    private int quarter;

    public void setQuarter(){
        if(date != null){
            int month = date.getMonthValue();
            if(month >= 1 && month <= 3)
                this.quarter = 1;
            else if(month >= 4 && month <= 6)
                this.quarter = 2;
            else if(month >= 7 && month <= 9)
                this.quarter = 3;
            else
                this.quarter = 4;
        }
    }

    public void addSubMaterial(SubMaterial material) {
        this.subMaterials.add(material);
    }

    public void addPicture(String url) {
        this.pictures.add(url);
    }
}