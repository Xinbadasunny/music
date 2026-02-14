package com.vocalcoach.adapter.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.vocalcoach.client.api.EvaluationServiceI;
import com.vocalcoach.client.dto.AudioAnalysisDTO;
import com.vocalcoach.client.dto.EvaluationResultDTO;
import com.vocalcoach.client.dto.cmd.AnalyzeAudioCmd;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/evaluation")
public class EvaluationController {

    private static final String AUDIO_DIR = "data/audios";

    @Resource
    private EvaluationServiceI evaluationService;

    @PostMapping("/upload")
    public SingleResponse<Map<String, String>> uploadAudio(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return SingleResponse.buildFailure("400", "上传文件不能为空");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return SingleResponse.buildFailure("400", "文件名不能为空");
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String fileExtension = getFileExtension(originalFilename);
            String newFilename = timestamp + "_" + originalFilename;

            Path uploadDir = Paths.get(AUDIO_DIR);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            Path filePath = uploadDir.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            Map<String, String> result = new HashMap<>();
            result.put("filename", newFilename);
            result.put("path", filePath.toString());
            result.put("originalFilename", originalFilename);
            result.put("size", String.valueOf(file.getSize()));
            result.put("contentType", file.getContentType());

            return SingleResponse.of(result);
        } catch (IOException e) {
            return SingleResponse.buildFailure("500", "文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/analyze")
    public SingleResponse<EvaluationResultDTO> analyzeAudio(@RequestBody AnalyzeAudioCmd cmd) {
        try {
            if (cmd.getAudioFilePath() == null || cmd.getAudioFilePath().isEmpty()) {
                return SingleResponse.buildFailure("400", "音频文件路径不能为空");
            }

            return evaluationService.evaluate(cmd);
        } catch (Exception e) {
            return SingleResponse.buildFailure("500", "音频分析失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public MultiResponse<EvaluationResultDTO> listEvaluations() {
        return evaluationService.listEvaluations();
    }

    @GetMapping("/{id}")
    public SingleResponse<EvaluationResultDTO> getEvaluation(@PathVariable Long id) {
        try {
            return evaluationService.getEvaluation(id);
        } catch (Exception e) {
            return SingleResponse.buildFailure("500", "获取评测详情失败: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex);
        }
        return "";
    }
}
