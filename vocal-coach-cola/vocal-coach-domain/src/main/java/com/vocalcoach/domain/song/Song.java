package com.vocalcoach.domain.song;

import lombok.Data;
import java.util.List;

@Data
public class Song {
    private Long id;
    private String name;
    private String artist;
    private Integer difficulty;
    private String category;
    private Integer bpm;
    private String key;
    private String timeSignature;
    private List<MelodyNote> melodyPattern;

    @Data
    public static class MelodyNote {
        private Integer note;
        private Integer duration;
        private String lyric;
    }
}
