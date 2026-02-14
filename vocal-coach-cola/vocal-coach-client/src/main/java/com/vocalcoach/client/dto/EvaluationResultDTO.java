package com.vocalcoach.client.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class EvaluationResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String songName;
    private String audioPath;
    private LocalDateTime evaluatedAt;

    private Scores scores;
    private AudioAnalysisDTO.Features features;

    private List<Strength> strengths;
    private List<Weakness> weaknesses;
    private List<Advice> advices;
    private List<CourseRecommendation> courseRecommendations;

    private String aiEvaluation;

    @Data
    public static class Scores implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer overall;
        private Integer pitch;
        private Integer rhythm;
        private Integer voice;
        private Integer breath;
        private Integer style;
    }

    @Data
    public static class Strength implements Serializable {
        private static final long serialVersionUID = 1L;
        private String dimension;
        private String title;
        private String description;
        private String icon;
    }

    @Data
    public static class Weakness implements Serializable {
        private static final long serialVersionUID = 1L;
        private String dimension;
        private String title;
        private String description;
        private String icon;
    }

    @Data
    public static class Advice implements Serializable {
        private static final long serialVersionUID = 1L;
        private String dimension;
        private String title;
        private String description;
        private Integer priority;
    }

    @Data
    public static class CourseRecommendation implements Serializable {
        private static final long serialVersionUID = 1L;
        private String courseId;
        private String courseName;
        private String courseIcon;
        private String reason;
        private Integer priority;
    }
}
