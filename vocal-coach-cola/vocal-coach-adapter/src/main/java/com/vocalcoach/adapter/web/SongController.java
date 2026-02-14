package com.vocalcoach.adapter.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.vocalcoach.client.api.SongServiceI;
import com.vocalcoach.client.dto.SongDTO;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Resource
    private SongServiceI songService;

    @GetMapping
    public MultiResponse<SongDTO> listSongs(@RequestParam(required = false) String category) {
        return songService.listSongs(category);
    }

    @GetMapping("/{id}")
    public SingleResponse<SongDTO> getSongById(@PathVariable Long id) {
        return songService.getSongById(id);
    }

    @GetMapping("/search")
    public MultiResponse<SongDTO> searchSongs(@RequestParam String keyword) {
        return songService.searchSongs(keyword);
    }
}
