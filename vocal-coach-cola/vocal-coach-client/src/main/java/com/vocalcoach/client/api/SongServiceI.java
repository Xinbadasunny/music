package com.vocalcoach.client.api;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.vocalcoach.client.dto.SongDTO;

public interface SongServiceI {

    MultiResponse<SongDTO> listSongs(String category);

    SingleResponse<SongDTO> getSongById(Long id);

    MultiResponse<SongDTO> searchSongs(String keyword);

    SingleResponse<SongDTO> createSong(SongDTO songDTO);
}
