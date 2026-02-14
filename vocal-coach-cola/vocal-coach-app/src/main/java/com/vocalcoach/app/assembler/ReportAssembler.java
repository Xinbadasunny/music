package com.vocalcoach.app.assembler;

import com.vocalcoach.client.dto.ReportDTO;
import com.vocalcoach.client.dto.cmd.SaveReportCmd;
import com.vocalcoach.domain.report.Report;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ReportAssembler {

    public ReportDTO toDTO(Report report) {
        if (report == null) {
            return null;
        }
        ReportDTO dto = new ReportDTO();
        dto.setId(report.getId());
        dto.setSongName(report.getSongName());
        dto.setOverallScore(report.getOverallScore());
        dto.setTimestamp(report.getTimestamp());

        if (report.getDimensions() != null) {
            ReportDTO.Dimensions dims = new ReportDTO.Dimensions();
            dims.setPitch(report.getDimensions().getPitch());
            dims.setRhythm(report.getDimensions().getRhythm());
            dims.setBreath(report.getDimensions().getBreath());
            dims.setVoice(report.getDimensions().getVoice());
            dto.setDimensions(dims);
        }

        if (report.getSuggestions() != null) {
            dto.setSuggestions(report.getSuggestions().stream()
                    .map(this::toSuggestionDTO)
                    .collect(Collectors.toList()));
        }

        if (report.getTrainingRecommendations() != null) {
            dto.setTrainingRecommendations(report.getTrainingRecommendations().stream()
                    .map(this::toRecommendationDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Report toEntity(SaveReportCmd cmd) {
        if (cmd == null) {
            return null;
        }
        Report report = new Report();
        report.setSongName(cmd.getSongName());
        report.setOverallScore(cmd.getOverallScore());

        if (cmd.getDimensions() != null) {
            Report.Dimensions dims = new Report.Dimensions();
            dims.setPitch(cmd.getDimensions().getPitch());
            dims.setRhythm(cmd.getDimensions().getRhythm());
            dims.setBreath(cmd.getDimensions().getBreath());
            dims.setVoice(cmd.getDimensions().getVoice());
            report.setDimensions(dims);
        }

        if (cmd.getSuggestions() != null) {
            report.setSuggestions(cmd.getSuggestions().stream()
                    .map(this::toSuggestionEntity)
                    .collect(Collectors.toList()));
        }

        if (cmd.getTrainingRecommendations() != null) {
            report.setTrainingRecommendations(cmd.getTrainingRecommendations().stream()
                    .map(this::toRecommendationEntity)
                    .collect(Collectors.toList()));
        }

        return report;
    }

    private ReportDTO.Suggestion toSuggestionDTO(Report.Suggestion suggestion) {
        ReportDTO.Suggestion dto = new ReportDTO.Suggestion();
        dto.setType(suggestion.getType());
        dto.setTitle(suggestion.getTitle());
        dto.setDescription(suggestion.getDescription());
        dto.setIcon(suggestion.getIcon());
        return dto;
    }

    private Report.Suggestion toSuggestionEntity(ReportDTO.Suggestion dto) {
        Report.Suggestion suggestion = new Report.Suggestion();
        suggestion.setType(dto.getType());
        suggestion.setTitle(dto.getTitle());
        suggestion.setDescription(dto.getDescription());
        suggestion.setIcon(dto.getIcon());
        return suggestion;
    }

    private ReportDTO.TrainingRecommendation toRecommendationDTO(Report.TrainingRecommendation rec) {
        ReportDTO.TrainingRecommendation dto = new ReportDTO.TrainingRecommendation();
        dto.setCourseId(rec.getCourseId());
        dto.setExerciseId(rec.getExerciseId());
        dto.setReason(rec.getReason());
        return dto;
    }

    private Report.TrainingRecommendation toRecommendationEntity(ReportDTO.TrainingRecommendation dto) {
        Report.TrainingRecommendation rec = new Report.TrainingRecommendation();
        rec.setCourseId(dto.getCourseId());
        rec.setExerciseId(dto.getExerciseId());
        rec.setReason(dto.getReason());
        return rec;
    }
}
