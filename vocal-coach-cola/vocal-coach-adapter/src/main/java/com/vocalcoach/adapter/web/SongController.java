package com.vocalcoach.adapter.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.vocalcoach.client.api.SongServiceI;
import com.vocalcoach.client.dto.SongDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/songs")
public class SongController {

    @Resource
    private SongServiceI songService;

    @Value("${data.path:data}")
    private String dataPath;

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

    @PostMapping("/upload")
    public SingleResponse<SongDTO> uploadSong(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("name") String name,
            @RequestParam(value = "artist", required = false, defaultValue = "未知") String artist,
            @RequestParam(value = "category", required = false, defaultValue = "流行") String category,
            @RequestParam(value = "difficulty", required = false, defaultValue = "2") Integer difficulty
    ) {
        try {
            Path songsDir = Paths.get(dataPath, "songs");
            if (!Files.exists(songsDir)) {
                Files.createDirectories(songsDir);
            }

            String originalFilename = audioFile.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID().toString() + extension;
            Path filePath = songsDir.resolve(filename);
            audioFile.transferTo(filePath.toFile());

            SongDTO songDTO = new SongDTO();
            songDTO.setName(name);
            songDTO.setArtist(artist);
            songDTO.setCategory(category);
            songDTO.setDifficulty(difficulty);
            songDTO.setAudioPath(filePath.toString());

            return songService.createSong(songDTO);
        } catch (IOException e) {
            return SingleResponse.buildFailure("UPLOAD_FAILED", "歌曲上传失败: " + e.getMessage());
        }
    }
}
