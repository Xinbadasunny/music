package com.vocalcoach.infrastructure.report.dataobject;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "report")
public class ReportDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String songName;

    @Column(nullable = false)
    private Integer overallScore;

    private Integer pitchScore;

    private Integer rhythmScore;

    private Integer breathScore;

    private Integer voiceScore;

    @Column(columnDefinition = "TEXT")
    private String suggestions;

    @Column(columnDefinition = "TEXT")
    private String trainingRecommendations;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
