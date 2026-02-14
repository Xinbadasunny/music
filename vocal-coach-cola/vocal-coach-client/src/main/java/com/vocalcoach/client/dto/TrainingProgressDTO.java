package com.vocalcoach.client.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TrainingProgressDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String courseId;
    private String exerciseId;
    private Integer bestScore;
    private Integer attempts;
    private Boolean completed;
    private LocalDateTime lastPracticeTime;
}
