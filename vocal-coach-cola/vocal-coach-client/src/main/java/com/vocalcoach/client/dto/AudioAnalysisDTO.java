package com.vocalcoach.client.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class AudioAnalysisDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String audioPath;
    private Features features;
    private Scores scores;

    @Data
    public static class Features implements Serializable {
        private static final long serialVersionUID = 1L;
        private PitchFeatures pitch;
        private RhythmFeatures rhythm;
        private VoiceFeatures voice;
        private TimbreFeatures timbre;
        private EnergyFeatures energy;
        private ComparisonFeatures comparison;
    }

    @Data
    public static class PitchFeatures implements Serializable {
        private static final long serialVersionUID = 1L;
        private Double meanPitch;
        private Double pitchRange;
        private Double pitchStability;
        private List<Double> pitchValues;
    }

    @Data
    public static class RhythmFeatures implements Serializable {
        private static final long serialVersionUID = 1L;
        private Double tempo;
        private Double beatRegularity;
        private Double rhythmScore;
    }

    @Data
    public static class VoiceFeatures implements Serializable {
        private static final long serialVersionUID = 1L;
        private Double jitter;
        private Double shimmer;
        private Double hnr;
        private Double jitterScore;
        private Double shimmerScore;
        private Double hnrScore;
        private Double voiceScore;
        private String voiceQuality;
    }

    @Data
    public static class TimbreFeatures implements Serializable {
        private static final long serialVersionUID = 1L;
        private List<Double> mfcc;
        private Double brightness;
        private Double warmth;
        private String brightnessLevel;
    }

    @Data
    public static class EnergyFeatures implements Serializable {
        private static final long serialVersionUID = 1L;
        private Double energyMean;
        private Double energyStability;
        private Double dynamicRange;
        private Double breathControlScore;
    }

    @Data
    public static class ComparisonFeatures implements Serializable {
        private static final long serialVersionUID = 1L;
        private Double dtwDistance;
        private Double similarityScore;
        private Boolean hasReference;
    }

    @Data
    public static class Scores implements Serializable {
        private static final long serialVersionUID = 1L;
        private Double overall;
        private Double pitch;
        private Double rhythm;
        private Double voice;
        private Double breath;
    }
}
