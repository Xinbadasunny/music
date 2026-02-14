package com.vocalcoach.client.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class StatisticsDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer totalReports;
    private Integer averageScore;
    private Integer bestScore;
    private Integer worstScore;
    private Integer averagePitch;
    private Integer averageRhythm;
    private Integer averageBreath;
    private Integer averageVoice;
    private Integer improvementTrend;
    private String recentTrend;
}
