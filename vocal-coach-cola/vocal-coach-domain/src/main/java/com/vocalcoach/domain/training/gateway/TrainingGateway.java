package com.vocalcoach.domain.training.gateway;

import com.vocalcoach.domain.training.Course;
import com.vocalcoach.domain.training.TrainingProgress;
import java.util.List;
import java.util.Optional;

public interface TrainingGateway {

    List<Course> findAllCourses();

    Optional<Course> findCourseById(String courseId);

    List<TrainingProgress> findAllProgress();

    Optional<TrainingProgress> findProgress(String courseId, String exerciseId);

    TrainingProgress saveProgress(TrainingProgress progress);

    int countCompletedExercises();

    int countTotalExercises();
}
