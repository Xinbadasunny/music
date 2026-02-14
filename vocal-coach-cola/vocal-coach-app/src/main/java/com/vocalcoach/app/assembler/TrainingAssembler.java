package com.vocalcoach.app.assembler;

import com.vocalcoach.client.dto.CourseDTO;
import com.vocalcoach.client.dto.TrainingProgressDTO;
import com.vocalcoach.domain.training.Course;
import com.vocalcoach.domain.training.TrainingProgress;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TrainingAssembler {

    public CourseDTO toDTO(Course course) {
        if (course == null) {
            return null;
        }
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setName(course.getName());
        dto.setIcon(course.getIcon());
        dto.setDescription(course.getDescription());

        if (course.getExercises() != null) {
            dto.setExercises(course.getExercises().stream()
                    .map(this::toExerciseDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public TrainingProgressDTO toDTO(TrainingProgress progress) {
        if (progress == null) {
            return null;
        }
        TrainingProgressDTO dto = new TrainingProgressDTO();
        dto.setId(progress.getId());
        dto.setCourseId(progress.getCourseId());
        dto.setExerciseId(progress.getExerciseId());
        dto.setBestScore(progress.getBestScore());
        dto.setAttempts(progress.getAttempts());
        dto.setCompleted(progress.getCompleted());
        dto.setLastPracticeTime(progress.getLastPracticeTime());
        return dto;
    }

    private CourseDTO.ExerciseDTO toExerciseDTO(Course.Exercise exercise) {
        CourseDTO.ExerciseDTO dto = new CourseDTO.ExerciseDTO();
        dto.setId(exercise.getId());
        dto.setName(exercise.getName());
        dto.setDescription(exercise.getDescription());
        dto.setBpm(exercise.getBpm());
        dto.setNotes(exercise.getNotes());
        dto.setPassingScore(exercise.getPassingScore());
        dto.setTips(exercise.getTips());
        return dto;
    }
}
