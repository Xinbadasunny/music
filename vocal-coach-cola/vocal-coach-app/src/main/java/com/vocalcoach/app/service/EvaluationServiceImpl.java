package com.vocalcoach.app.service;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.vocalcoach.app.assembler.EvaluationAssembler;
import com.vocalcoach.client.api.EvaluationServiceI;
import com.vocalcoach.client.dto.cmd.AnalyzeAudioCmd;
import com.vocalcoach.client.dto.AudioAnalysisDTO;
import com.vocalcoach.client.dto.EvaluationResultDTO;
import com.vocalcoach.domain.evaluation.Evaluation;
import com.vocalcoach.domain.evaluation.gateway.EvaluationGateway;
import com.vocalcoach.infrastructure.evaluation.PythonAudioAnalyzer;
import com.vocalcoach.infrastructure.ai.ClaudeApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class EvaluationServiceImpl implements EvaluationServiceI {

    @Autowired
    private PythonAudioAnalyzer pythonAudioAnalyzer;

    @Autowired
    private EvaluationGateway evaluationGateway;

    @Autowired
    private EvaluationAssembler evaluationAssembler;

    @Autowired
    private ClaudeApiClient claudeApiClient;

    @Override
    public SingleResponse<AudioAnalysisDTO> analyzeAudio(String audioFilePath, String referenceAudioPath) {
        AudioAnalysisDTO analysisResult = pythonAudioAnalyzer.analyze(audioFilePath, referenceAudioPath);
        return SingleResponse.of(analysisResult);
    }

    @Override
    public SingleResponse<EvaluationResultDTO> evaluate(AnalyzeAudioCmd cmd) {
        AudioAnalysisDTO analysisResult = pythonAudioAnalyzer.analyze(cmd.getAudioFilePath(), cmd.getReferenceAudioPath());

        if (!analysisResult.isSuccess()) {
            return SingleResponse.buildFailure("AUDIO_ANALYSIS_FAILED", "音频分析失败");
        }

        Evaluation evaluation = new Evaluation();
        evaluation.setSongName(cmd.getSongName());
        evaluation.setAudioPath(cmd.getAudioFilePath());
        evaluation.setReferenceAudioPath(cmd.getReferenceAudioPath());
        evaluation.setEvaluatedAt(LocalDateTime.now());

        if (analysisResult.getScores() != null) {
            Evaluation.Scores scores = new Evaluation.Scores();
            scores.setOverall(convertScore(analysisResult.getScores().getOverall()));
            scores.setPitch(convertScore(analysisResult.getScores().getPitch()));
            scores.setRhythm(convertScore(analysisResult.getScores().getRhythm()));
            scores.setVoice(convertScore(analysisResult.getScores().getVoice()));
            scores.setBreath(convertScore(analysisResult.getScores().getBreath()));
            scores.setStyle(convertScore(analysisResult.getScores().getOverall()));
            evaluation.setScores(scores);
        }

        if (analysisResult.getFeatures() != null) {
            evaluation.setFeatures(convertFeatures(analysisResult.getFeatures()));
        }

        ClaudeApiClient.EvaluationResult aiResult = claudeApiClient.generateEvaluation(
                analysisResult.getScores(),
                analysisResult.getFeatures(),
                cmd.getSongName()
        );

        if (aiResult != null) {
            evaluation.setAiEvaluation(aiResult.getOverallComment());
            if (aiResult.getStyleScore() != null && evaluation.getScores() != null) {
                evaluation.getScores().setStyle(aiResult.getStyleScore());
            }

            if (aiResult.getStrengths() != null) {
                evaluation.setStrengths(aiResult.getStrengths().stream()
                        .map(s -> {
                            Evaluation.Strength strength = new Evaluation.Strength();
                            strength.setDimension(s.getDimension());
                            strength.setTitle(s.getTitle());
                            strength.setDescription(s.getDescription());
                            strength.setIcon(s.getIcon());
                            return strength;
                        })
                        .collect(Collectors.toList()));
            }

            if (aiResult.getWeaknesses() != null) {
                evaluation.setWeaknesses(aiResult.getWeaknesses().stream()
                        .map(w -> {
                            Evaluation.Weakness weakness = new Evaluation.Weakness();
                            weakness.setDimension(w.getDimension());
                            weakness.setTitle(w.getTitle());
                            weakness.setDescription(w.getDescription());
                            weakness.setIcon(w.getIcon());
                            return weakness;
                        })
                        .collect(Collectors.toList()));
            }

            if (aiResult.getAdvices() != null) {
                evaluation.setAdvices(aiResult.getAdvices().stream()
                        .map(a -> {
                            Evaluation.Advice advice = new Evaluation.Advice();
                            advice.setDimension(a.getDimension());
                            advice.setTitle(a.getTitle());
                            advice.setDescription(a.getDescription());
                            advice.setPriority(a.getPriority());
                            return advice;
                        })
                        .collect(Collectors.toList()));
            }

            if (aiResult.getCourseRecommendations() != null) {
                evaluation.setCourseRecommendations(aiResult.getCourseRecommendations().stream()
                        .map(c -> {
                            Evaluation.CourseRecommendation course = new Evaluation.CourseRecommendation();
                            course.setCourseId(c.getCourseId());
                            course.setCourseName(c.getCourseName());
                            course.setCourseIcon(c.getCourseIcon());
                            course.setReason(c.getReason());
                            course.setPriority(c.getPriority());
                            return course;
                        })
                        .collect(Collectors.toList()));
            }
        }

        Evaluation savedEvaluation = evaluationGateway.save(evaluation);
        EvaluationResultDTO resultDTO = evaluationAssembler.toDTO(savedEvaluation);

        return SingleResponse.of(resultDTO);
    }

    @Override
    public MultiResponse<EvaluationResultDTO> listEvaluations() {
        return MultiResponse.of(
                evaluationGateway.findAll()
                        .stream()
                        .map(evaluationAssembler::toDTO)
                        .collect(Collectors.toList())
        );
    }

    @Override
    public SingleResponse<EvaluationResultDTO> getEvaluation(Long id) {
        return evaluationGateway.findById(id)
                .map(evaluationAssembler::toDTO)
                .map(SingleResponse::of)
                .orElse(SingleResponse.buildFailure("EVALUATION_NOT_FOUND", "评估记录不存在"));
    }

    private Integer convertScore(Double score) {
        if (score == null) {
            return 0;
        }
        return (int) Math.round(score);
    }

    private Evaluation.AudioFeatures convertFeatures(AudioAnalysisDTO.Features features) {
        Evaluation.AudioFeatures audioFeatures = new Evaluation.AudioFeatures();

        if (features.getPitch() != null) {
            Evaluation.PitchFeatures pitch = new Evaluation.PitchFeatures();
            pitch.setMeanPitch(features.getPitch().getMeanPitch());
            pitch.setPitchRange(features.getPitch().getPitchRange());
            pitch.setPitchStability(features.getPitch().getPitchStability());
            audioFeatures.setPitch(pitch);
        }

        if (features.getRhythm() != null) {
            Evaluation.RhythmFeatures rhythm = new Evaluation.RhythmFeatures();
            rhythm.setTempo(features.getRhythm().getTempo());
            rhythm.setBeatRegularity(features.getRhythm().getBeatRegularity());
            rhythm.setRhythmScore(features.getRhythm().getRhythmScore());
            audioFeatures.setRhythm(rhythm);
        }

        if (features.getVoice() != null) {
            Evaluation.VoiceFeatures voice = new Evaluation.VoiceFeatures();
            voice.setJitter(features.getVoice().getJitter());
            voice.setShimmer(features.getVoice().getShimmer());
            voice.setHnr(features.getVoice().getHnr());
            voice.setVoiceScore(features.getVoice().getVoiceScore());
            voice.setVoiceQuality(features.getVoice().getVoiceQuality());
            audioFeatures.setVoice(voice);
        }

        if (features.getTimbre() != null) {
            Evaluation.TimbreFeatures timbre = new Evaluation.TimbreFeatures();
            timbre.setBrightness(features.getTimbre().getBrightness());
            timbre.setWarmth(features.getTimbre().getWarmth());
            timbre.setBrightnessLevel(features.getTimbre().getBrightnessLevel());
            audioFeatures.setTimbre(timbre);
        }

        if (features.getEnergy() != null) {
            Evaluation.EnergyFeatures energy = new Evaluation.EnergyFeatures();
            energy.setEnergyMean(features.getEnergy().getEnergyMean());
            energy.setEnergyStability(features.getEnergy().getEnergyStability());
            energy.setBreathControlScore(features.getEnergy().getBreathControlScore());
            audioFeatures.setEnergy(energy);
        }

        return audioFeatures;
    }
}