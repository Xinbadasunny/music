package com.vocalcoach.infrastructure.training.dataobject;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "training_progress")
public class TrainingProgressDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String courseId;

    @Column(nullable = false)
    private String exerciseId;

    private Integer bestScore;

    private Integer attempts;

    private Boolean completed;

    private LocalDateTime lastPracticeTime;
}
