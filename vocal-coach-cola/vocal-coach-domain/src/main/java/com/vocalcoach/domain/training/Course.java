package com.vocalcoach.domain.training;

import lombok.Data;
import java.util.List;

@Data
public class Course {
    private String id;
    private String name;
    private String icon;
    private String description;
    private List<Exercise> exercises;

    @Data
    public static class Exercise {
        private String id;
        private String name;
        private String description;
        private Integer bpm;
        private List<Integer> notes;
        private Integer passingScore;
        private String tips;
    }
}
