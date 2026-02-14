package com.vocalcoach.client.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReportDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String songName;
    private Integer overallScore;
    private Dimensions dimensions;
    private List<Suggestion> suggestions;
    private List<TrainingRecommendation> trainingRecommendations;
    private LocalDateTime timestamp;

    @Data
    public static class Dimensions implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer pitch;
        private Integer rhythm;
        private Integer breath;
        private Integer voice;
    }

    @Data
    public static class Suggestion implements Serializable {
        private static final long serialVersionUID = 1L;
        private String type;
        private String title;
        private String description;
        private String icon;
    }

    @Data
    public static class TrainingRecommendation implements Serializable {
        private static final long serialVersionUID = 1L;
        private String courseId;
        private String exerciseId;
        private String reason;
    }
}
