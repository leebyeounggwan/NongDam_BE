package com.example.formproject.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private LocalDateTime startTime;

    @Column
    private LocalDateTime endTime;

    @Column
    private String toDo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Crop crop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Member member;
}
