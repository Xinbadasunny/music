package com.vocalcoach.domain.evaluation;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Evaluation {
    private Long id;
    private String songName;
    private String audioPath;
    private String referenceAudioPath;
    private LocalDateTime evaluatedAt;

    private Scores scores;
    private AudioFeatures features;

    private List<Strength> strengths;
    private List<Weakness> weaknesses;
    private List<Advice> advices;
    private List<CourseRecommendation> courseRecommendations;

    private String aiEvaluation;

    @Data
    public static class Scores {
        private Integer overall;
        private Integer pitch;
        private Integer rhythm;
        private Integer voice;
        private Integer breath;
        private Integer style;
    }

    @Data
    public static class AudioFeatures {
        private PitchFeatures pitch;
        private RhythmFeatures rhythm;
        private VoiceFeatures voice;
        private TimbreFeatures timbre;
        private EnergyFeatures energy;
    }

    @Data
    public static class PitchFeatures {
        private Double meanPitch;
        private Double pitchRange;
        private Double pitchStability;
    }

    @Data
    public static class RhythmFeatures {
        private Double tempo;
        private Double beatRegularity;
        private Double rhythmScore;
    }

    @Data
    public static class VoiceFeatures {
        private Double jitter;
        private Double shimmer;
        private Double hnr;
        private Double voiceScore;
        private String voiceQuality;
    }

    @Data
    public static class TimbreFeatures {
        private Double brightness;
        private Double warmth;
        private String brightnessLevel;
    }

    @Data
    public static class EnergyFeatures {
        private Double energyMean;
        private Double energyStability;
        private Double breathControlScore;
    }

    @Data
    public static class Strength {
        private String dimension;
        private String title;
        private String description;
        private String icon;
    }

    @Data
    public static class Weakness {
        private String dimension;
        private String title;
        private String description;
        private String icon;
    }

    @Data
    public static class Advice {
        private String dimension;
        private String title;
        private String description;
        private Integer priority;
    }

    @Data
    public static class CourseRecommendation {
        private String courseId;
        private String courseName;
        private String courseIcon;
        private String reason;
        private Integer priority;
    }
}
