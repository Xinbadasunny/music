package com.vocalcoach.infrastructure.training.repository;

import com.vocalcoach.infrastructure.training.dataobject.TrainingProgressDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingProgressRepository extends JpaRepository<TrainingProgressDO, Long> {

    Optional<TrainingProgressDO> findByCourseIdAndExerciseId(String courseId, String exerciseId);

    @Query("SELECT COUNT(p) FROM TrainingProgressDO p WHERE p.completed = true")
    int countByCompletedTrue();
}
