package com.vocalcoach.domain.training;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrainingProgress {
    private Long id;
    private String courseId;
    private String exerciseId;
    private Integer bestScore;
    private Integer attempts;
    private Boolean completed;
    private LocalDateTime lastPracticeTime;

    public void updateProgress(Integer newScore) {
        this.attempts = (this.attempts == null ? 0 : this.attempts) + 1;
        if (this.bestScore == null || newScore > this.bestScore) {
            this.bestScore = newScore;
        }
        if (newScore >= 80) {
            this.completed = true;
        }
        this.lastPracticeTime = LocalDateTime.now();
    }
}
