package com.vocalcoach.infrastructure.song.gateway;

import com.alibaba.fastjson.JSON;
import com.vocalcoach.domain.song.Song;
import com.vocalcoach.domain.song.gateway.SongGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class SongGatewayImpl implements SongGateway {

    private static final String DATA_DIR = "data";
    private static final String SONGS_FILE = "songs.json";

    private List<Song> songsCache = new ArrayList<>();
    private AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void init() {
        ensureDataDir();
        loadFromFile();
        if (songsCache.isEmpty()) {
            initDefaultSongs();
        }
    }

    private void ensureDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private void loadFromFile() {
        File file = new File(DATA_DIR, SONGS_FILE);
        if (file.exists()) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                songsCache = JSON.parseArray(content, Song.class);
                if (songsCache == null) {
                    songsCache = new ArrayList<>();
                }
                long maxId = songsCache.stream().mapToLong(s -> s.getId() != null ? s.getId() : 0).max().orElse(0);
                idGenerator.set(maxId + 1);
            } catch (IOException e) {
                songsCache = new ArrayList<>();
            }
        }
    }

    private void saveToFile() {
        try {
            String content = JSON.toJSONString(songsCache);
            Files.write(Paths.get(DATA_DIR, SONGS_FILE), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDefaultSongs() {
        createDefaultSongs().forEach(this::save);
    }

    private List<Song> createDefaultSongs() {
        List<Song> songs = new ArrayList<>();

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

    private Song createSong(String name, String artist, int difficulty, String category,
                            int bpm, String key, String timeSignature, String melodyPatternJson) {
        Song song = new Song();
        song.setName(name);
        song.setArtist(artist);
        song.setDifficulty(difficulty);
        song.setCategory(category);
        song.setBpm(bpm);
        song.setKey(key);
        song.setTimeSignature(timeSignature);
        try {
            List<Song.MelodyNote> notes = JSON.parseArray(melodyPatternJson, Song.MelodyNote.class);
            song.setMelodyPattern(notes);
        } catch (Exception e) {
            song.setMelodyPattern(new ArrayList<>());
        }
        return song;
    }

    @Override
    public List<Song> findAll() {
        return new ArrayList<>(songsCache);
    }

    @Override
    public List<Song> findByCategory(String category) {
        return songsCache.stream()
                .filter(s -> category.equals(s.getCategory()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Song> findById(Long id) {
        return songsCache.stream()
                .filter(s -> id.equals(s.getId()))
                .findFirst();
    }

    @Override
    public List<Song> search(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return songsCache.stream()
                .filter(s -> (s.getName() != null && s.getName().toLowerCase().contains(lowerKeyword))
                        || (s.getArtist() != null && s.getArtist().toLowerCase().contains(lowerKeyword)))
                .collect(Collectors.toList());
    }

    @Override
    public Song save(Song song) {
        if (song.getId() == null) {
            song.setId(idGenerator.getAndIncrement());
            songsCache.add(song);
        } else {
            songsCache.removeIf(s -> song.getId().equals(s.getId()));
            songsCache.add(song);
        }
        saveToFile();
        return song;
    }

    @Override
    public long count() {
        return songsCache.size();
    }

    // ==================== 以下为数据库实现代码，待数据库就绪后启用 ====================
    // @Resource
    // private SongRepository songRepository;
    //
    // @Override
    // public List<Song> findAll() {
    //     return songRepository.findAll().stream()
    //             .map(this::toEntity)
    //             .collect(Collectors.toList());
    // }
    //
    // @Override
    // public List<Song> findByCategory(String category) {
    //     return songRepository.findByCategory(category).stream()
    //             .map(this::toEntity)
    //             .collect(Collectors.toList());
    // }
    //
    // @Override
    // public Optional<Song> findById(Long id) {
    //     return songRepository.findById(id).map(this::toEntity);
    // }
    //
    // @Override
    // public List<Song> search(String keyword) {
    //     return songRepository.search(keyword).stream()
    //             .map(this::toEntity)
    //             .collect(Collectors.toList());
    // }
    //
    // @Override
    // public Song save(Song song) {
    //     SongDO songDO = toDO(song);
    //     SongDO saved = songRepository.save(songDO);
    //     return toEntity(saved);
    // }
    //
    // @Override
    // public long count() {
    //     return songRepository.count();
    // }
    //
    // private Song toEntity(SongDO songDO) {
    //     Song song = new Song();
    //     song.setId(songDO.getId());
    //     song.setName(songDO.getName());
    //     song.setArtist(songDO.getArtist());
    //     song.setDifficulty(songDO.getDifficulty());
    //     song.setCategory(songDO.getCategory());
    //     song.setBpm(songDO.getBpm());
    //     song.setKey(songDO.getKey());
    //     song.setTimeSignature(songDO.getTimeSignature());
    //     if (songDO.getMelodyPattern() != null) {
    //         try {
    //             List<Song.MelodyNote> notes = JSON.parseArray(songDO.getMelodyPattern(), Song.MelodyNote.class);
    //             song.setMelodyPattern(notes);
    //         } catch (Exception e) {
    //             song.setMelodyPattern(new ArrayList<>());
    //         }
    //     }
    //     return song;
    // }
    //
    // private SongDO toDO(Song song) {
    //     SongDO songDO = new SongDO();
    //     songDO.setId(song.getId());
    //     songDO.setName(song.getName());
    //     songDO.setArtist(song.getArtist());
    //     songDO.setDifficulty(song.getDifficulty());
    //     songDO.setCategory(song.getCategory());
    //     songDO.setBpm(song.getBpm());
    //     songDO.setKey(song.getKey());
    //     songDO.setTimeSignature(song.getTimeSignature());
    //     if (song.getMelodyPattern() != null) {
    //         songDO.setMelodyPattern(JSON.toJSONString(song.getMelodyPattern()));
    //     }
    //     return songDO;
    // }
}
