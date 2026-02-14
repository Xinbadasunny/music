package com.vocalcoach.domain.report;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Report {
    private Long id;
    private String songName;
    private Integer overallScore;
    private Dimensions dimensions;
    private List<Suggestion> suggestions;
    private List<TrainingRecommendation> trainingRecommendations;
    private LocalDateTime timestamp;

    @Data
    public static class Dimensions {
        private Integer pitch;
        private Integer rhythm;
        private Integer breath;
        private Integer voice;
    }

    @Data
    public static class Suggestion {
        private String type;
        private String title;
        private String description;
        private String icon;
    }

    @Data
    public static class TrainingRecommendation {
        private String courseId;
        private String exerciseId;
        private String reason;
    }
}
