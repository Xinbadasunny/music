package com.vocalcoach.client.dto.cmd;

import lombok.Data;
import java.io.Serializable;

@Data
public class AnalyzeAudioCmd implements Serializable {
    private static final long serialVersionUID = 1L;

    private String songName;
    private String audioFilePath;
    private String referenceAudioPath;
}
