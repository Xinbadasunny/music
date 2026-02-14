package com.vocalcoach.infrastructure.evaluation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vocalcoach.client.dto.AudioAnalysisDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class PythonAudioAnalyzer {

    @Value("${python.path:python3}")
    private String pythonPath;

    @Value("${scripts.path:scripts}")
    private String scriptsPath;

    public AudioAnalysisDTO analyze(String audioFilePath, String referenceAudioPath) {
        try {
            List<String> command = new ArrayList<>();
            command.add(pythonPath);
            command.add(new File(scriptsPath, "analyze_audio.py").getAbsolutePath());
            command.add(audioFilePath);
            if (referenceAudioPath != null && !referenceAudioPath.isEmpty()) {
                command.add(referenceAudioPath);
            }

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            processBuilder.directory(new File(System.getProperty("user.dir")));

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }

            boolean finished = process.waitFor(120, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                return createErrorResult("Python 脚本执行超时");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                return createErrorResult("Python 脚本执行失败: " + output.toString());
            }

            return parseAnalysisResult(output.toString());

        } catch (Exception e) {
            return createErrorResult("音频分析失败: " + e.getMessage());
        }
    }

    private AudioAnalysisDTO parseAnalysisResult(String jsonOutput) {
        try {
            JSONObject json = JSON.parseObject(jsonOutput);

            AudioAnalysisDTO result = new AudioAnalysisDTO();
            result.setSuccess(json.getBooleanValue("success"));
            result.setAudioPath(json.getString("audio_path"));

            JSONObject featuresJson = json.getJSONObject("features");
            if (featuresJson != null) {
                AudioAnalysisDTO.Features features = new AudioAnalysisDTO.Features();

                JSONObject pitchJson = featuresJson.getJSONObject("pitch");
                if (pitchJson != null && !pitchJson.containsKey("error")) {
                    AudioAnalysisDTO.PitchFeatures pitch = new AudioAnalysisDTO.PitchFeatures();
                    pitch.setMeanPitch(pitchJson.getDouble("mean_pitch"));
                    pitch.setPitchRange(pitchJson.getDouble("pitch_range"));
                    pitch.setPitchStability(pitchJson.getDouble("pitch_stability"));
                    features.setPitch(pitch);
                }

                JSONObject rhythmJson = featuresJson.getJSONObject("rhythm");
                if (rhythmJson != null && !rhythmJson.containsKey("error")) {
                    AudioAnalysisDTO.RhythmFeatures rhythm = new AudioAnalysisDTO.RhythmFeatures();
                    rhythm.setTempo(rhythmJson.getDouble("tempo"));
                    rhythm.setBeatRegularity(rhythmJson.getDouble("beat_regularity"));
                    rhythm.setRhythmScore(rhythmJson.getDouble("rhythm_score"));
                    features.setRhythm(rhythm);
                }

                JSONObject voiceJson = featuresJson.getJSONObject("voice");
                if (voiceJson != null && !voiceJson.containsKey("error")) {
                    AudioAnalysisDTO.VoiceFeatures voice = new AudioAnalysisDTO.VoiceFeatures();
                    voice.setJitter(voiceJson.getDouble("jitter"));
                    voice.setShimmer(voiceJson.getDouble("shimmer"));
                    voice.setHnr(voiceJson.getDouble("hnr"));
                    voice.setJitterScore(voiceJson.getDouble("jitter_score"));
                    voice.setShimmerScore(voiceJson.getDouble("shimmer_score"));
                    voice.setHnrScore(voiceJson.getDouble("hnr_score"));
                    voice.setVoiceScore(voiceJson.getDouble("voice_score"));
                    voice.setVoiceQuality(voiceJson.getString("voice_quality"));
                    features.setVoice(voice);
                }

                JSONObject timbreJson = featuresJson.getJSONObject("timbre");
                if (timbreJson != null && !timbreJson.containsKey("error")) {
                    AudioAnalysisDTO.TimbreFeatures timbre = new AudioAnalysisDTO.TimbreFeatures();
                    timbre.setBrightness(timbreJson.getDouble("brightness"));
                    timbre.setWarmth(timbreJson.getDouble("warmth"));
                    timbre.setBrightnessLevel(timbreJson.getString("brightness_level"));
                    features.setTimbre(timbre);
                }

                JSONObject energyJson = featuresJson.getJSONObject("energy");
                if (energyJson != null && !energyJson.containsKey("error")) {
                    AudioAnalysisDTO.EnergyFeatures energy = new AudioAnalysisDTO.EnergyFeatures();
                    energy.setEnergyMean(energyJson.getDouble("energy_mean"));
                    energy.setEnergyStability(energyJson.getDouble("energy_stability"));
                    energy.setDynamicRange(energyJson.getDouble("dynamic_range"));
                    energy.setBreathControlScore(energyJson.getDouble("breath_control_score"));
                    features.setEnergy(energy);
                }

                JSONObject comparisonJson = featuresJson.getJSONObject("comparison");
                if (comparisonJson != null) {
                    AudioAnalysisDTO.ComparisonFeatures comparison = new AudioAnalysisDTO.ComparisonFeatures();
                    comparison.setHasReference(comparisonJson.getBooleanValue("has_reference"));
                    if (comparison.getHasReference()) {
                        comparison.setDtwDistance(comparisonJson.getDouble("dtw_distance"));
                        comparison.setSimilarityScore(comparisonJson.getDouble("similarity_score"));
                    }
                    features.setComparison(comparison);
                }

                result.setFeatures(features);
            }

            JSONObject scoresJson = json.getJSONObject("scores");
            if (scoresJson != null) {
                AudioAnalysisDTO.Scores scores = new AudioAnalysisDTO.Scores();
                scores.setOverall(scoresJson.getDouble("overall"));
                scores.setPitch(scoresJson.getDouble("pitch"));
                scores.setRhythm(scoresJson.getDouble("rhythm"));
                scores.setVoice(scoresJson.getDouble("voice"));
                scores.setBreath(scoresJson.getDouble("breath"));
                result.setScores(scores);
            }

            return result;

        } catch (Exception e) {
            return createErrorResult("解析分析结果失败: " + e.getMessage());
        }
    }

    private AudioAnalysisDTO createErrorResult(String errorMessage) {
        AudioAnalysisDTO result = new AudioAnalysisDTO();
        result.setSuccess(false);
        return result;
    }
}
