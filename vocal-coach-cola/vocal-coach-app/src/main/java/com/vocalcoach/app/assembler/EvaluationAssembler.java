package com.vocalcoach.app.assembler;

import com.vocalcoach.client.dto.AudioAnalysisDTO;
import com.vocalcoach.client.dto.EvaluationResultDTO;
import com.vocalcoach.domain.evaluation.Evaluation;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EvaluationAssembler {

    public EvaluationResultDTO toDTO(Evaluation evaluation) {
        if (evaluation == null) {
            return null;
        }

        EvaluationResultDTO dto = new EvaluationResultDTO();
        dto.setId(evaluation.getId());
        dto.setSongName(evaluation.getSongName());
        dto.setAudioPath(evaluation.getAudioPath());
        dto.setEvaluatedAt(evaluation.getEvaluatedAt());
        dto.setAiEvaluation(evaluation.getAiEvaluation());

        if (evaluation.getScores() != null) {
            dto.setScores(convertScores(evaluation.getScores()));
        }

        if (evaluation.getFeatures() != null) {
            dto.setFeatures(convertFeatures(evaluation.getFeatures()));
        }

        if (evaluation.getStrengths() != null) {
            dto.setStrengths(
                    evaluation.getStrengths().stream()
                            .map(this::convertStrength)
                            .collect(Collectors.toList())
            );
        }

        if (evaluation.getWeaknesses() != null) {
            dto.setWeaknesses(
                    evaluation.getWeaknesses().stream()
                            .map(this::convertWeakness)
                            .collect(Collectors.toList())
            );
        }

        if (evaluation.getAdvices() != null) {
            dto.setAdvices(
                    evaluation.getAdvices().stream()
                            .map(this::convertAdvice)
                            .collect(Collectors.toList())
            );
        }

        if (evaluation.getCourseRecommendations() != null) {
            dto.setCourseRecommendations(
                    evaluation.getCourseRecommendations().stream()
                            .map(this::convertCourseRecommendation)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    private EvaluationResultDTO.Scores convertScores(Evaluation.Scores scores) {
        EvaluationResultDTO.Scores dto = new EvaluationResultDTO.Scores();
        dto.setOverall(scores.getOverall());
        dto.setPitch(scores.getPitch());
        dto.setRhythm(scores.getRhythm());
        dto.setVoice(scores.getVoice());
        dto.setBreath(scores.getBreath());
        dto.setStyle(scores.getStyle());
        return dto;
    }

    private AudioAnalysisDTO.Features convertFeatures(Evaluation.AudioFeatures features) {
        AudioAnalysisDTO.Features dto = new AudioAnalysisDTO.Features();

        if (features.getPitch() != null) {
            dto.setPitch(convertPitchFeatures(features.getPitch()));
        }

        if (features.getRhythm() != null) {
            dto.setRhythm(convertRhythmFeatures(features.getRhythm()));
        }

        if (features.getVoice() != null) {
            dto.setVoice(convertVoiceFeatures(features.getVoice()));
        }

        if (features.getTimbre() != null) {
            dto.setTimbre(convertTimbreFeatures(features.getTimbre()));
        }

        if (features.getEnergy() != null) {
            dto.setEnergy(convertEnergyFeatures(features.getEnergy()));
        }

        return dto;
    }

    private AudioAnalysisDTO.PitchFeatures convertPitchFeatures(Evaluation.PitchFeatures pitch) {
        AudioAnalysisDTO.PitchFeatures dto = new AudioAnalysisDTO.PitchFeatures();
        dto.setMeanPitch(pitch.getMeanPitch());
        dto.setPitchRange(pitch.getPitchRange());
        dto.setPitchStability(pitch.getPitchStability());
        return dto;
    }

    private AudioAnalysisDTO.RhythmFeatures convertRhythmFeatures(Evaluation.RhythmFeatures rhythm) {
        AudioAnalysisDTO.RhythmFeatures dto = new AudioAnalysisDTO.RhythmFeatures();
        dto.setTempo(rhythm.getTempo());
        dto.setBeatRegularity(rhythm.getBeatRegularity());
        dto.setRhythmScore(rhythm.getRhythmScore());
        return dto;
    }

    private AudioAnalysisDTO.VoiceFeatures convertVoiceFeatures(Evaluation.VoiceFeatures voice) {
        AudioAnalysisDTO.VoiceFeatures dto = new AudioAnalysisDTO.VoiceFeatures();
        dto.setJitter(voice.getJitter());
        dto.setShimmer(voice.getShimmer());
        dto.setHnr(voice.getHnr());
        dto.setVoiceScore(voice.getVoiceScore());
        dto.setVoiceQuality(voice.getVoiceQuality());
        return dto;
    }

    private AudioAnalysisDTO.TimbreFeatures convertTimbreFeatures(Evaluation.TimbreFeatures timbre) {
        AudioAnalysisDTO.TimbreFeatures dto = new AudioAnalysisDTO.TimbreFeatures();
        dto.setBrightness(timbre.getBrightness());
        dto.setWarmth(timbre.getWarmth());
        dto.setBrightnessLevel(timbre.getBrightnessLevel());
        return dto;
    }

    private AudioAnalysisDTO.EnergyFeatures convertEnergyFeatures(Evaluation.EnergyFeatures energy) {
        AudioAnalysisDTO.EnergyFeatures dto = new AudioAnalysisDTO.EnergyFeatures();
        dto.setEnergyMean(energy.getEnergyMean());
        dto.setEnergyStability(energy.getEnergyStability());
        dto.setBreathControlScore(energy.getBreathControlScore());
        return dto;
    }

    private EvaluationResultDTO.Strength convertStrength(Evaluation.Strength strength) {
        EvaluationResultDTO.Strength dto = new EvaluationResultDTO.Strength();
        dto.setDimension(strength.getDimension());
        dto.setTitle(strength.getTitle());
        dto.setDescription(strength.getDescription());
        dto.setIcon(strength.getIcon());
        return dto;
    }

    private EvaluationResultDTO.Weakness convertWeakness(Evaluation.Weakness weakness) {
        EvaluationResultDTO.Weakness dto = new EvaluationResultDTO.Weakness();
        dto.setDimension(weakness.getDimension());
        dto.setTitle(weakness.getTitle());
        dto.setDescription(weakness.getDescription());
        dto.setIcon(weakness.getIcon());
        return dto;
    }

    private EvaluationResultDTO.Advice convertAdvice(Evaluation.Advice advice) {
        EvaluationResultDTO.Advice dto = new EvaluationResultDTO.Advice();
        dto.setDimension(advice.getDimension());
        dto.setTitle(advice.getTitle());
        dto.setDescription(advice.getDescription());
        dto.setPriority(advice.getPriority());
        return dto;
    }

    private EvaluationResultDTO.CourseRecommendation convertCourseRecommendation(Evaluation.CourseRecommendation recommendation) {
        EvaluationResultDTO.CourseRecommendation dto = new EvaluationResultDTO.CourseRecommendation();
        dto.setCourseId(recommendation.getCourseId());
        dto.setCourseName(recommendation.getCourseName());
        dto.setCourseIcon(recommendation.getCourseIcon());
        dto.setReason(recommendation.getReason());
        dto.setPriority(recommendation.getPriority());
        return dto;
    }
}