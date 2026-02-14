package com.vocalcoach.client.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class CourseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String icon;
    private String description;
    private List<ExerciseDTO> exercises;

    @Data
    public static class ExerciseDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        private String id;
        private String name;
        private String description;
        private Integer bpm;
        private List<Integer> notes;
        private Integer passingScore;
        private String tips;
    }
}
