package com.vocalcoach.client.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class SongDTO implements Serializable {
    private static final long serialVersionUID = 1L;

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
    public static class MelodyNote implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer note;
        private Integer duration;
        private String lyric;
    }
}
