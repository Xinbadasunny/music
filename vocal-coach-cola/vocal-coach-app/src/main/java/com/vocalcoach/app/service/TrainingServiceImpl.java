package com.vocalcoach.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;

import com.vocalcoach.app.assembler.TrainingAssembler;
import com.vocalcoach.client.api.TrainingServiceI;
import com.vocalcoach.client.dto.CourseDTO;
import com.vocalcoach.client.dto.TrainingProgressDTO;
import com.vocalcoach.client.dto.cmd.SaveProgressCmd;
import com.vocalcoach.domain.training.Course;
import com.vocalcoach.domain.training.TrainingProgress;
import com.vocalcoach.domain.training.gateway.TrainingGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingServiceImpl implements TrainingServiceI {

    @Resource
    private TrainingGateway trainingGateway;

    @Resource
    private TrainingAssembler trainingAssembler;

    @Override
    public MultiResponse<CourseDTO> listCourses() {
        List<Course> courses = trainingGateway.findAllCourses();
        List<CourseDTO> dtoList = courses.stream()
            .map(trainingAssembler::toDTO)
            .collect(Collectors.toList());
        return MultiResponse.of(dtoList);
    }

    @Override
    public SingleResponse<Map<String, Map<String, TrainingProgressDTO>>> getProgress() {
        List<TrainingProgress> progressList = trainingGateway.findAllProgress();
        Map<String, Map<String, TrainingProgressDTO>> progressMap = new HashMap<>();

        for (TrainingProgress progress : progressList) {
            String courseId = progress.getCourseId();
            String exerciseId = progress.getExerciseId();

            progressMap.computeIfAbsent(courseId, k -> new HashMap<>());
            progressMap.get(courseId).put(exerciseId, trainingAssembler.toDTO(progress));
        }

        return SingleResponse.of(progressMap);
    }

    @Override
    @Transactional
    public SingleResponse<TrainingProgressDTO> saveProgress(SaveProgressCmd cmd) {
        TrainingProgress progress = trainingGateway
            .findProgress(cmd.getCourseId(), cmd.getExerciseId())
            .orElseGet(() -> {
                TrainingProgress newProgress = new TrainingProgress();
                newProgress.setCourseId(cmd.getCourseId());
                newProgress.setExerciseId(cmd.getExerciseId());
                newProgress.setAttempts(0);
                newProgress.setCompleted(false);
                return newProgress;
            });

        progress.updateProgress(cmd.getScore());
        TrainingProgress savedProgress = trainingGateway.saveProgress(progress);
        return SingleResponse.of(trainingAssembler.toDTO(savedProgress));
    }

    @Override
    public SingleResponse<Integer> getOverallProgress() {
        int completed = trainingGateway.countCompletedExercises();
        int total = trainingGateway.countTotalExercises();
        int percentage = total > 0 ? (completed * 100) / total : 0;
        return SingleResponse.of(percentage);
    }

    @Override
    public SingleResponse<Integer> getCompletedCount() {
        int count = trainingGateway.countCompletedExercises();
        return SingleResponse.of(count);
    }
}
