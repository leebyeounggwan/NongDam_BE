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
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

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
    private long harvest = 0;
}
