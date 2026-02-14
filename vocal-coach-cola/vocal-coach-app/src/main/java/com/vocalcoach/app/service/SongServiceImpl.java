package com.vocalcoach.app.service;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.vocalcoach.app.assembler.SongAssembler;
import com.vocalcoach.client.api.SongServiceI;
import com.vocalcoach.client.dto.SongDTO;
import com.vocalcoach.domain.song.Song;
import com.vocalcoach.domain.song.gateway.SongGateway;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl implements SongServiceI {

    @Resource
    private SongGateway songGateway;

    @Resource
    private SongAssembler songAssembler;

    @Override
    public MultiResponse<SongDTO> listSongs(String category) {
        List<Song> songs;
        if (category == null || category.isEmpty() || "全部".equals(category)) {
            songs = songGateway.findAll();
        } else {
            songs = songGateway.findByCategory(category);
        }
        List<SongDTO> dtoList = songs.stream()
                .map(songAssembler::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtoList);
    }

    @Override
    public SingleResponse<SongDTO> getSongById(Long id) {
        return songGateway.findById(id)
                .map(song -> SingleResponse.of(songAssembler.toDTO(song)))
                .orElse(SingleResponse.buildFailure("SONG_NOT_FOUND", "歌曲不存在"));
    }

    @Override
    public MultiResponse<SongDTO> searchSongs(String keyword) {
        List<Song> songs = songGateway.search(keyword);
        List<SongDTO> dtoList = songs.stream()
                .map(songAssembler::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtoList);
    }

    @Override
    public SingleResponse<SongDTO> createSong(SongDTO songDTO) {
        Song song = songAssembler.toEntity(songDTO);
        song.setId(songGateway.count() + 1);
        Song savedSong = songGateway.save(song);
        return SingleResponse.of(songAssembler.toDTO(savedSong));
    }
}
