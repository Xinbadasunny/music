package com.vocalcoach.domain.song.gateway;

import com.vocalcoach.domain.song.Song;
import java.util.List;
import java.util.Optional;

public interface SongGateway {

    List<Song> findAll();

    List<Song> findByCategory(String category);

    Optional<Song> findById(Long id);

    List<Song> search(String keyword);

    Song save(Song song);

    long count();
}
