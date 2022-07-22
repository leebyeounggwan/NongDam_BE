package com.example.formproject.entity;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.SubMaterialRequestDto;
import com.example.formproject.dto.request.WorkLogRequestDto;
import com.example.formproject.repository.CropRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
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

    @Column
    private int workTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Crop crop;

    @Column
    private String memo;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<SubMaterial> subMaterials = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "worklog_pictures", joinColumns = {@JoinColumn(name = "work_log_id", referencedColumnName = "id")})
    @Column
    @Builder.Default
    private List<String> images = new ArrayList<>();

    @Column
    @Builder.Default
    private long harvest = 0L;

    @Column
    private int quarter;

    public void updateWorkLog(WorkLogRequestDto requestDto, Crop crop, List<SubMaterial> SubMaterialList) {
        this.title = requestDto.getTitle();
        this.date = LocalDate.parse(requestDto.getDate(), FinalValue.DAY_FORMATTER);
        this.workTime = requestDto.getWorkTime();
        this.crop = crop;
        this.memo = requestDto.getMemo();
        this.subMaterials.clear();
        this.subMaterials.addAll(SubMaterialList);
        if (requestDto.getImages() != null) this.images.clear();
        this.harvest = requestDto.getHarvest();
    }

    public void setQuarter() {
        if (date != null) {
            int month = date.getMonthValue();
            if (month >= 1 && month <= 3)
                this.quarter = 1;
            else if (month >= 4 && month <= 6)
                this.quarter = 2;
            else if (month >= 7 && month <= 9)
                this.quarter = 3;
            else
                this.quarter = 4;
        }
    }

    public void addSubMaterial(SubMaterial material) {
        this.subMaterials.add(material);
    }

    public void addPicture(String url) {
        this.images.add(url);
    }
}