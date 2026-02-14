package com.vocalcoach.adapter.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.vocalcoach.client.api.TrainingServiceI;
import com.vocalcoach.client.dto.CourseDTO;
import com.vocalcoach.client.dto.TrainingProgressDTO;
import com.vocalcoach.client.dto.cmd.SaveProgressCmd;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/training")
public class TrainingController {

    @Resource
    private TrainingServiceI trainingService;

    @GetMapping("/courses")
    public MultiResponse<CourseDTO> listCourses() {
        return trainingService.listCourses();
    }

    @GetMapping("/progress")
    public SingleResponse<Map<String, Map<String, TrainingProgressDTO>>> getProgress() {
        return trainingService.getProgress();
    }

    @PostMapping("/progress")
    public SingleResponse<TrainingProgressDTO> saveProgress(@Valid @RequestBody SaveProgressCmd cmd) {
        return trainingService.saveProgress(cmd);
    }

    @GetMapping("/overall-progress")
    public SingleResponse<Integer> getOverallProgress() {
        return trainingService.getOverallProgress();
    }

    @GetMapping("/completed-count")
    public SingleResponse<Integer> getCompletedCount() {
        return trainingService.getCompletedCount();
    }
}
