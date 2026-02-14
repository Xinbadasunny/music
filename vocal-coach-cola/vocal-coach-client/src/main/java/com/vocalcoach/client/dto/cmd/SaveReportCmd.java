package com.vocalcoach.client.dto.cmd;

import com.vocalcoach.client.dto.ReportDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
public class SaveReportCmd implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "歌曲名称不能为空")
    private String songName;

    @NotNull(message = "总分不能为空")
    private Integer overallScore;

    @NotNull(message = "维度评分不能为空")
    private ReportDTO.Dimensions dimensions;

    private List<ReportDTO.Suggestion> suggestions;

    private List<ReportDTO.TrainingRecommendation> trainingRecommendations;
}
