package com.vocalcoach.infrastructure.song.gateway;

import com.alibaba.fastjson.JSON;
import com.vocalcoach.domain.song.Song;
import com.vocalcoach.domain.song.gateway.SongGateway;
import com.vocalcoach.infrastructure.song.dataobject.SongDO;
import com.vocalcoach.infrastructure.song.repository.SongRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SongGatewayImpl implements SongGateway {

    @Resource
    private SongRepository songRepository;

    @PostConstruct
    public void initDefaultSongs() {
        if (songRepository.count() == 0) {
            List<SongDO> defaultSongs = createDefaultSongs();
            songRepository.saveAll(defaultSongs);
        }
    }

    private List<SongDO> createDefaultSongs() {
        List<SongDO> songs = new ArrayList<>();

        songs.add(createSong("小星星", "儿歌", 1, "儿歌", 90, "C", "4/4",
                "[{\"note\":60,\"duration\":1,\"lyric\":\"一\"},{\"note\":60,\"duration\":1,\"lyric\":\"闪\"},{\"note\":67,\"duration\":1,\"lyric\":\"一\"},{\"note\":67,\"duration\":1,\"lyric\":\"闪\"},{\"note\":69,\"duration\":1,\"lyric\":\"亮\"},{\"note\":69,\"duration\":1,\"lyric\":\"晶\"},{\"note\":67,\"duration\":2,\"lyric\":\"晶\"}]"));

        songs.add(createSong("两只老虎", "儿歌", 1, "儿歌", 100, "C", "4/4",
                "[{\"note\":60,\"duration\":1,\"lyric\":\"两\"},{\"note\":62,\"duration\":1,\"lyric\":\"只\"},{\"note\":64,\"duration\":1,\"lyric\":\"老\"},{\"note\":60,\"duration\":1,\"lyric\":\"虎\"}]"));

        songs.add(createSong("月亮代表我的心", "邓丽君", 2, "经典", 72, "G", "4/4",
                "[{\"note\":67,\"duration\":1,\"lyric\":\"你\"},{\"note\":69,\"duration\":1,\"lyric\":\"问\"},{\"note\":71,\"duration\":1,\"lyric\":\"我\"},{\"note\":72,\"duration\":1,\"lyric\":\"爱\"}]"));

        songs.add(createSong("甜蜜蜜", "邓丽君", 2, "经典", 85, "F", "4/4",
                "[{\"note\":65,\"duration\":1,\"lyric\":\"甜\"},{\"note\":67,\"duration\":1,\"lyric\":\"蜜\"},{\"note\":69,\"duration\":1,\"lyric\":\"蜜\"}]"));

        songs.add(createSong("晴天", "周杰伦", 3, "流行", 74, "G", "4/4",
                "[{\"note\":67,\"duration\":1,\"lyric\":\"故\"},{\"note\":69,\"duration\":1,\"lyric\":\"事\"},{\"note\":71,\"duration\":1,\"lyric\":\"的\"}]"));

        songs.add(createSong("稻香", "周杰伦", 2, "流行", 88, "G", "4/4",
                "[{\"note\":67,\"duration\":1,\"lyric\":\"对\"},{\"note\":69,\"duration\":1,\"lyric\":\"这\"},{\"note\":71,\"duration\":1,\"lyric\":\"个\"}]"));

        songs.add(createSong("夜曲", "周杰伦", 4, "流行", 76, "Cm", "4/4",
                "[{\"note\":60,\"duration\":1,\"lyric\":\"一\"},{\"note\":63,\"duration\":1,\"lyric\":\"群\"},{\"note\":65,\"duration\":1,\"lyric\":\"嗜\"}]"));

        songs.add(createSong("青花瓷", "周杰伦", 3, "流行", 68, "G", "4/4",
                "[{\"note\":67,\"duration\":1,\"lyric\":\"素\"},{\"note\":69,\"duration\":1,\"lyric\":\"胚\"},{\"note\":71,\"duration\":1,\"lyric\":\"勾\"}]"));

        return songs;
    }

    private SongDO createSong(String name, String artist, int difficulty, String category,
                               int bpm, String key, String timeSignature, String melodyPattern) {
        SongDO song = new SongDO();
        song.setName(name);
        song.setArtist(artist);
        song.setDifficulty(difficulty);
        song.setCategory(category);
        song.setBpm(bpm);
        song.setKey(key);
        song.setTimeSignature(timeSignature);
        song.setMelodyPattern(melodyPattern);
        return song;
    }

    @Override
    public List<Song> findAll() {
        return songRepository.findAll().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Song> findByCategory(String category) {
        return songRepository.findByCategory(category).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Song> findById(Long id) {
        return songRepository.findById(id).map(this::toEntity);
    }

    @Override
    public List<Song> search(String keyword) {
        return songRepository.search(keyword).stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Song save(Song song) {
        SongDO songDO = toDO(song);
        SongDO saved = songRepository.save(songDO);
        return toEntity(saved);
    }

    @Override
    public long count() {
        return songRepository.count();
    }

    private Song toEntity(SongDO songDO) {
        Song song = new Song();
        song.setId(songDO.getId());
        song.setName(songDO.getName());
        song.setArtist(songDO.getArtist());
        song.setDifficulty(songDO.getDifficulty());
        song.setCategory(songDO.getCategory());
        song.setBpm(songDO.getBpm());
        song.setKey(songDO.getKey());
        song.setTimeSignature(songDO.getTimeSignature());

        if (songDO.getMelodyPattern() != null) {
            try {
                List<Song.MelodyNote> notes = JSON.parseArray(songDO.getMelodyPattern(), Song.MelodyNote.class);
                song.setMelodyPattern(notes);
            } catch (Exception e) {
                song.setMelodyPattern(new ArrayList<>());
            }
        }
        return song;
    }

    private SongDO toDO(Song song) {
        SongDO songDO = new SongDO();
        songDO.setId(song.getId());
        songDO.setName(song.getName());
        songDO.setArtist(song.getArtist());
        songDO.setDifficulty(song.getDifficulty());
        songDO.setCategory(song.getCategory());
        songDO.setBpm(song.getBpm());
        songDO.setKey(song.getKey());
        songDO.setTimeSignature(song.getTimeSignature());

        if (song.getMelodyPattern() != null) {
            songDO.setMelodyPattern(JSON.toJSONString(song.getMelodyPattern()));
        }
        return songDO;
    }
}
