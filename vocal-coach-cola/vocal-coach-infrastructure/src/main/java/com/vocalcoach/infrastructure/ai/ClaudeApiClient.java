package com.vocalcoach.infrastructure.ai;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vocalcoach.client.dto.AudioAnalysisDTO;
import com.vocalcoach.client.dto.EvaluationResultDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ClaudeApiClient {

    @Value("${claude.api.key:}")
    private String apiKey;

    @Value("${claude.api.url:https://api.anthropic.com/v1/messages}")
    private String apiUrl;

    @Value("${claude.api.model:claude-3-sonnet-20240229}")
    private String model;

    public EvaluationResult generateEvaluation(AudioAnalysisDTO.Scores scores, AudioAnalysisDTO.Features features, String songName) {
        if (apiKey == null || apiKey.isEmpty()) {
            return generateMockEvaluation(scores, features, songName);
        }

        try {
            String prompt = buildEvaluationPrompt(scores, features, songName);
            String response = callClaudeApi(prompt);
            return parseEvaluationResponse(response, scores);
        } catch (Exception e) {
            return generateMockEvaluation(scores, features, songName);
        }
    }

    private String buildEvaluationPrompt(AudioAnalysisDTO.Scores scores, AudioAnalysisDTO.Features features, String songName) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一位专业的声乐教练，请根据以下音频分析数据，为用户的演唱提供专业评价。\n\n");
        sb.append("歌曲名称：").append(songName != null ? songName : "未知").append("\n\n");

        sb.append("【评分数据】\n");
        if (scores != null) {
            sb.append("- 综合得分：").append(scores.getOverall()).append("/100\n");
            sb.append("- 音准得分：").append(scores.getPitch()).append("/100\n");
            sb.append("- 节奏得分：").append(scores.getRhythm()).append("/100\n");
            sb.append("- 嗓音得分：").append(scores.getVoice()).append("/100\n");
            sb.append("- 气息得分：").append(scores.getBreath()).append("/100\n");
        }

        sb.append("\n【详细特征数据】\n");
        if (features != null) {
            if (features.getVoice() != null) {
                sb.append("- 嗓音质量：").append(features.getVoice().getVoiceQuality()).append("\n");
                sb.append("- 谐波噪声比(HNR)：").append(String.format("%.2f", features.getVoice().getHnr())).append("dB\n");
                sb.append("- 频率抖动(Jitter)：").append(String.format("%.4f", features.getVoice().getJitter())).append("\n");
                sb.append("- 振幅抖动(Shimmer)：").append(String.format("%.4f", features.getVoice().getShimmer())).append("\n");
            }
            if (features.getRhythm() != null) {
                sb.append("- 节拍速度：").append(String.format("%.1f", features.getRhythm().getTempo())).append(" BPM\n");
                sb.append("- 节拍规律性：").append(String.format("%.1f", features.getRhythm().getBeatRegularity())).append("%\n");
            }
            if (features.getPitch() != null) {
                sb.append("- 平均音高：").append(String.format("%.1f", features.getPitch().getMeanPitch())).append(" Hz\n");
                sb.append("- 音高稳定性：").append(String.format("%.1f", features.getPitch().getPitchStability())).append("%\n");
            }
            if (features.getTimbre() != null) {
                sb.append("- 音色明亮度：").append(features.getTimbre().getBrightnessLevel()).append("\n");
            }
        }

        sb.append("\n请按以下JSON格式返回评价结果：\n");
        sb.append("{\n");
        sb.append("  \"strengths\": [{\"dimension\": \"维度\", \"title\": \"优点标题\", \"description\": \"详细描述\", \"icon\": \"emoji图标\"}],\n");
        sb.append("  \"weaknesses\": [{\"dimension\": \"维度\", \"title\": \"缺点标题\", \"description\": \"详细描述\", \"icon\": \"emoji图标\"}],\n");
        sb.append("  \"advices\": [{\"dimension\": \"维度\", \"title\": \"建议标题\", \"description\": \"详细建议\", \"priority\": 1}],\n");
        sb.append("  \"courseRecommendations\": [{\"courseId\": \"课程ID\", \"courseName\": \"课程名称\", \"courseIcon\": \"emoji\", \"reason\": \"推荐原因\", \"priority\": 1}],\n");
        sb.append("  \"overallComment\": \"总体评价文字\",\n");
        sb.append("  \"styleScore\": 风格得分(0-100)\n");
        sb.append("}\n");
        sb.append("\n课程ID可选值：scale(音阶训练)、breath(气息训练)、rhythm(节奏训练)、pitch(音准训练)、vibrato(颤音训练)、range(音域拓展)\n");

        return sb.toString();
    }

    private String callClaudeApi(String prompt) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("x-api-key", apiKey);
        conn.setRequestProperty("anthropic-version", "2023-06-01");
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(60000);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", model);
        requestBody.put("max_tokens", 2000);

        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", prompt);
        messages.add(message);
        requestBody.put("messages", messages);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toJSONString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        StringBuilder response = new StringBuilder();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        if (responseCode >= 400) {
            throw new RuntimeException("Claude API 调用失败: " + response.toString());
        }

        JSONObject responseJson = JSON.parseObject(response.toString());
        JSONArray content = responseJson.getJSONArray("content");
        if (content != null && !content.isEmpty()) {
            return content.getJSONObject(0).getString("text");
        }

        return null;
    }

    private EvaluationResult parseEvaluationResponse(String response, AudioAnalysisDTO.Scores scores) {
        EvaluationResult result = new EvaluationResult();

        try {
            int jsonStart = response.indexOf("{");
            int jsonEnd = response.lastIndexOf("}");
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                String jsonStr = response.substring(jsonStart, jsonEnd + 1);
                JSONObject json = JSON.parseObject(jsonStr);

                result.setOverallComment(json.getString("overallComment"));
                result.setStyleScore(json.getInteger("styleScore"));

                JSONArray strengthsArray = json.getJSONArray("strengths");
                if (strengthsArray != null) {
                    List<EvaluationResultDTO.Strength> strengths = new ArrayList<>();
                    for (int i = 0; i < strengthsArray.size(); i++) {
                        JSONObject item = strengthsArray.getJSONObject(i);
                        EvaluationResultDTO.Strength strength = new EvaluationResultDTO.Strength();
                        strength.setDimension(item.getString("dimension"));
                        strength.setTitle(item.getString("title"));
                        strength.setDescription(item.getString("description"));
                        strength.setIcon(item.getString("icon"));
                        strengths.add(strength);
                    }
                    result.setStrengths(strengths);
                }

                JSONArray weaknessesArray = json.getJSONArray("weaknesses");
                if (weaknessesArray != null) {
                    List<EvaluationResultDTO.Weakness> weaknesses = new ArrayList<>();
                    for (int i = 0; i < weaknessesArray.size(); i++) {
                        JSONObject item = weaknessesArray.getJSONObject(i);
                        EvaluationResultDTO.Weakness weakness = new EvaluationResultDTO.Weakness();
                        weakness.setDimension(item.getString("dimension"));
                        weakness.setTitle(item.getString("title"));
                        weakness.setDescription(item.getString("description"));
                        weakness.setIcon(item.getString("icon"));
                        weaknesses.add(weakness);
                    }
                    result.setWeaknesses(weaknesses);
                }

                JSONArray advicesArray = json.getJSONArray("advices");
                if (advicesArray != null) {
                    List<EvaluationResultDTO.Advice> advices = new ArrayList<>();
                    for (int i = 0; i < advicesArray.size(); i++) {
                        JSONObject item = advicesArray.getJSONObject(i);
                        EvaluationResultDTO.Advice advice = new EvaluationResultDTO.Advice();
                        advice.setDimension(item.getString("dimension"));
                        advice.setTitle(item.getString("title"));
                        advice.setDescription(item.getString("description"));
                        advice.setPriority(item.getInteger("priority"));
                        advices.add(advice);
                    }
                    result.setAdvices(advices);
                }

                JSONArray coursesArray = json.getJSONArray("courseRecommendations");
                if (coursesArray != null) {
                    List<EvaluationResultDTO.CourseRecommendation> courses = new ArrayList<>();
                    for (int i = 0; i < coursesArray.size(); i++) {
                        JSONObject item = coursesArray.getJSONObject(i);
                        EvaluationResultDTO.CourseRecommendation course = new EvaluationResultDTO.CourseRecommendation();
                        course.setCourseId(item.getString("courseId"));
                        course.setCourseName(item.getString("courseName"));
                        course.setCourseIcon(item.getString("courseIcon"));
                        course.setReason(item.getString("reason"));
                        course.setPriority(item.getInteger("priority"));
                        courses.add(course);
                    }
                    result.setCourseRecommendations(courses);
                }
            }
        } catch (Exception e) {
            return generateMockEvaluation(scores, null, null);
        }

        return result;
    }

    private EvaluationResult generateMockEvaluation(AudioAnalysisDTO.Scores scores, AudioAnalysisDTO.Features features, String songName) {
        EvaluationResult result = new EvaluationResult();

        List<EvaluationResultDTO.Strength> strengths = new ArrayList<>();
        List<EvaluationResultDTO.Weakness> weaknesses = new ArrayList<>();
        List<EvaluationResultDTO.Advice> advices = new ArrayList<>();
        List<EvaluationResultDTO.CourseRecommendation> courses = new ArrayList<>();

        if (scores != null) {
            if (scores.getPitch() != null && scores.getPitch() >= 70) {
                EvaluationResultDTO.Strength s = new EvaluationResultDTO.Strength();
                s.setDimension("音准");
                s.setTitle("音准把控良好");
                s.setDescription("您的音准表现稳定，能够准确把握歌曲的旋律走向。");
                s.setIcon("🎯");
                strengths.add(s);
            } else if (scores.getPitch() != null && scores.getPitch() < 70) {
                EvaluationResultDTO.Weakness w = new EvaluationResultDTO.Weakness();
                w.setDimension("音准");
                w.setTitle("音准需要加强");
                w.setDescription("部分音符存在偏差，建议多进行音阶练习来提升音准感知能力。");
                w.setIcon("🎵");
                weaknesses.add(w);

                EvaluationResultDTO.CourseRecommendation c = new EvaluationResultDTO.CourseRecommendation();
                c.setCourseId("pitch");
                c.setCourseName("音准训练");
                c.setCourseIcon("🎯");
                c.setReason("提升音准感知和控制能力");
                c.setPriority(1);
                courses.add(c);
            }

            if (scores.getRhythm() != null && scores.getRhythm() >= 70) {
                EvaluationResultDTO.Strength s = new EvaluationResultDTO.Strength();
                s.setDimension("节奏");
                s.setTitle("节奏感强");
                s.setDescription("您能够很好地跟随歌曲节拍，节奏把控到位。");
                s.setIcon("🥁");
                strengths.add(s);
            } else if (scores.getRhythm() != null && scores.getRhythm() < 70) {
                EvaluationResultDTO.Weakness w = new EvaluationResultDTO.Weakness();
                w.setDimension("节奏");
                w.setTitle("节奏感需提升");
                w.setDescription("演唱时存在抢拍或拖拍现象，建议配合节拍器练习。");
                w.setIcon("⏱️");
                weaknesses.add(w);

                EvaluationResultDTO.CourseRecommendation c = new EvaluationResultDTO.CourseRecommendation();
                c.setCourseId("rhythm");
                c.setCourseName("节奏训练");
                c.setCourseIcon("🥁");
                c.setReason("增强节奏感和节拍掌控能力");
                c.setPriority(2);
                courses.add(c);
            }

            if (scores.getVoice() != null && scores.getVoice() >= 70) {
                EvaluationResultDTO.Strength s = new EvaluationResultDTO.Strength();
                s.setDimension("嗓音");
                s.setTitle("嗓音状态良好");
                s.setDescription("声音清澈稳定，共鸣运用得当。");
                s.setIcon("🎤");
                strengths.add(s);
            } else if (scores.getVoice() != null && scores.getVoice() < 70) {
                EvaluationResultDTO.Weakness w = new EvaluationResultDTO.Weakness();
                w.setDimension("嗓音");
                w.setTitle("嗓音控制需改善");
                w.setDescription("声音存在不稳定或挤压现象，建议放松喉咙，注意气息支撑。");
                w.setIcon("🔊");
                weaknesses.add(w);
            }

            if (scores.getBreath() != null && scores.getBreath() >= 70) {
                EvaluationResultDTO.Strength s = new EvaluationResultDTO.Strength();
                s.setDimension("气息");
                s.setTitle("气息控制稳定");
                s.setDescription("气息运用流畅，长音保持稳定。");
                s.setIcon("🌬️");
                strengths.add(s);
            } else if (scores.getBreath() != null && scores.getBreath() < 70) {
                EvaluationResultDTO.Weakness w = new EvaluationResultDTO.Weakness();
                w.setDimension("气息");
                w.setTitle("气息支撑不足");
                w.setDescription("长音时气息不够稳定，建议加强腹式呼吸训练。");
                w.setIcon("💨");
                weaknesses.add(w);

                EvaluationResultDTO.CourseRecommendation c = new EvaluationResultDTO.CourseRecommendation();
                c.setCourseId("breath");
                c.setCourseName("气息训练");
                c.setCourseIcon("🌬️");
                c.setReason("增强气息控制和呼吸稳定性");
                c.setPriority(1);
                courses.add(c);
            }
        }

        EvaluationResultDTO.Advice advice1 = new EvaluationResultDTO.Advice();
        advice1.setDimension("综合");
        advice1.setTitle("坚持每日练习");
        advice1.setDescription("建议每天进行15-30分钟的声乐练习，包括发声练习和歌曲演唱。");
        advice1.setPriority(1);
        advices.add(advice1);

        EvaluationResultDTO.Advice advice2 = new EvaluationResultDTO.Advice();
        advice2.setDimension("技巧");
        advice2.setTitle("注意热身");
        advice2.setDescription("演唱前进行充分的声带热身，避免直接演唱高难度歌曲。");
        advice2.setPriority(2);
        advices.add(advice2);

        result.setStrengths(strengths);
        result.setWeaknesses(weaknesses);
        result.setAdvices(advices);
        result.setCourseRecommendations(courses);
        result.setStyleScore(scores != null && scores.getOverall() != null ? (int)(scores.getOverall() * 0.9) : 70);

        StringBuilder comment = new StringBuilder();
        comment.append("整体表现");
        if (scores != null && scores.getOverall() != null) {
            if (scores.getOverall() >= 80) {
                comment.append("优秀！");
            } else if (scores.getOverall() >= 60) {
                comment.append("良好，");
            } else {
                comment.append("有待提升，");
            }
        }
        comment.append("继续保持练习，相信您会越唱越好！");
        result.setOverallComment(comment.toString());

        return result;
    }

    public static class EvaluationResult {
        private List<EvaluationResultDTO.Strength> strengths;
        private List<EvaluationResultDTO.Weakness> weaknesses;
        private List<EvaluationResultDTO.Advice> advices;
        private List<EvaluationResultDTO.CourseRecommendation> courseRecommendations;
        private String overallComment;
        private Integer styleScore;

        public List<EvaluationResultDTO.Strength> getStrengths() { return strengths; }
        public void setStrengths(List<EvaluationResultDTO.Strength> strengths) { this.strengths = strengths; }
        public List<EvaluationResultDTO.Weakness> getWeaknesses() { return weaknesses; }
        public void setWeaknesses(List<EvaluationResultDTO.Weakness> weaknesses) { this.weaknesses = weaknesses; }
        public List<EvaluationResultDTO.Advice> getAdvices() { return advices; }
        public void setAdvices(List<EvaluationResultDTO.Advice> advices) { this.advices = advices; }
        public List<EvaluationResultDTO.CourseRecommendation> getCourseRecommendations() { return courseRecommendations; }
        public void setCourseRecommendations(List<EvaluationResultDTO.CourseRecommendation> courseRecommendations) { this.courseRecommendations = courseRecommendations; }
        public String getOverallComment() { return overallComment; }
        public void setOverallComment(String overallComment) { this.overallComment = overallComment; }
        public Integer getStyleScore() { return styleScore; }
        public void setStyleScore(Integer styleScore) { this.styleScore = styleScore; }
    }
}
