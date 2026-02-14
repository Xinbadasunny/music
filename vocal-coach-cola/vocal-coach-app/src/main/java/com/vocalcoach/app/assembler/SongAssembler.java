package com.vocalcoach.app.assembler;

import com.vocalcoach.client.dto.SongDTO;
import com.vocalcoach.domain.song.Song;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SongAssembler {

    public SongDTO toDTO(Song song) {
        if (song == null) {
            return null;
        }
        SongDTO dto = new SongDTO();
        dto.setId(song.getId());
        dto.setName(song.getName());
        dto.setArtist(song.getArtist());
        dto.setDifficulty(song.getDifficulty());
        dto.setCategory(song.getCategory());
        dto.setBpm(song.getBpm());
        dto.setKey(song.getKey());
        dto.setTimeSignature(song.getTimeSignature());

        if (song.getMelodyPattern() != null) {
            dto.setMelodyPattern(song.getMelodyPattern().stream()
                    .map(this::toMelodyNoteDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public Song toEntity(SongDTO dto) {
        if (dto == null) {
            return null;
        }
        Song song = new Song();
        song.setId(dto.getId());
        song.setName(dto.getName());
        song.setArtist(dto.getArtist());
        song.setDifficulty(dto.getDifficulty());
        song.setCategory(dto.getCategory());
        song.setBpm(dto.getBpm());
        song.setKey(dto.getKey());
        song.setTimeSignature(dto.getTimeSignature());

        if (dto.getMelodyPattern() != null) {
            song.setMelodyPattern(dto.getMelodyPattern().stream()
                    .map(this::toMelodyNoteEntity)
                    .collect(Collectors.toList()));
        }
        return song;
    }

    private SongDTO.MelodyNote toMelodyNoteDTO(Song.MelodyNote note) {
        SongDTO.MelodyNote dto = new SongDTO.MelodyNote();
        dto.setNote(note.getNote());
        dto.setDuration(note.getDuration());
        dto.setLyric(note.getLyric());
        return dto;
    }

    private Song.MelodyNote toMelodyNoteEntity(SongDTO.MelodyNote dto) {
        Song.MelodyNote note = new Song.MelodyNote();
        note.setNote(dto.getNote());
        note.setDuration(dto.getDuration());
        note.setLyric(dto.getLyric());
        return note;
    }
}
