package com.vocalcoach.infrastructure.song.dataobject;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "song")
public class SongDO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String artist;

    private Integer difficulty;

    private String category;

    private Integer bpm;

    @Column(name = "song_key")
    private String key;

    private String timeSignature;

    @Column(columnDefinition = "TEXT")
    private String melodyPattern;
}
