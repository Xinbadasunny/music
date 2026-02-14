package com.vocalcoach.infrastructure.report.gateway;

import com.alibaba.fastjson.JSON;
import com.vocalcoach.domain.report.Report;
import com.vocalcoach.domain.report.gateway.ReportGateway;
import com.vocalcoach.infrastructure.report.dataobject.ReportDO;
import com.vocalcoach.infrastructure.report.repository.ReportJpaRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ReportGatewayImpl implements ReportGateway {

    @Resource
    private ReportJpaRepository reportRepository;

    @Override
    public Report save(Report report) {
        ReportDO reportDO = toDO(report);
        ReportDO saved = reportRepository.save(reportDO);
        return toEntity(saved);
    }

    @Override
    public List<Report> findAll() {
        return reportRepository.findAll().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Report> findById(Long id) {
        return reportRepository.findById(id).map(this::toEntity);
    }

    @Override
    public void deleteById(Long id) {
        reportRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return reportRepository.existsById(id);
    }

    @Override
    public long count() {
        return reportRepository.count();
    }

    @Override
    public Double getAverageScore() {
        return reportRepository.getAverageScore();
    }

    @Override
    public Integer getBestScore() {
        return reportRepository.getBestScore();
    }

    @Override
    public Integer getWorstScore() {
        return reportRepository.getWorstScore();
    }

    @Override
    public Double getAveragePitchScore() {
        return reportRepository.getAveragePitchScore();
    }

    @Override
    public Double getAverageRhythmScore() {
        return reportRepository.getAverageRhythmScore();
    }

    @Override
    public Double getAverageBreathScore() {
        return reportRepository.getAverageBreathScore();
    }

    @Override
    public Double getAverageVoiceScore() {
        return reportRepository.getAverageVoiceScore();
    }

    @Override
    public List<Report> findTop10ByOrderByTimestampDesc() {
        return reportRepository.findTop10ByOrderByTimestampDesc().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    private Report toEntity(ReportDO reportDO) {
        Report report = new Report();
        report.setId(reportDO.getId());
        report.setSongName(reportDO.getSongName());
        report.setOverallScore(reportDO.getOverallScore());
        report.setTimestamp(reportDO.getTimestamp());

        Report.Dimensions dims = new Report.Dimensions();
        dims.setPitch(reportDO.getPitchScore());
        dims.setRhythm(reportDO.getRhythmScore());
        dims.setBreath(reportDO.getBreathScore());
        dims.setVoice(reportDO.getVoiceScore());
        report.setDimensions(dims);

        if (reportDO.getSuggestions() != null) {
            try {
                List<Report.Suggestion> suggestions = JSON.parseArray(reportDO.getSuggestions(), Report.Suggestion.class);
                report.setSuggestions(suggestions);
            } catch (Exception e) {
                report.setSuggestions(new ArrayList<>());
            }
        }

        if (reportDO.getTrainingRecommendations() != null) {
            try {
                List<Report.TrainingRecommendation> recommendations = JSON.parseArray(reportDO.getTrainingRecommendations(), Report.TrainingRecommendation.class);
                report.setTrainingRecommendations(recommendations);
            } catch (Exception e) {
                report.setTrainingRecommendations(new ArrayList<>());
            }
        }

        return report;
    }

    private ReportDO toDO(Report report) {
        ReportDO reportDO = new ReportDO();
        reportDO.setId(report.getId());
        reportDO.setSongName(report.getSongName());
        reportDO.setOverallScore(report.getOverallScore());
        reportDO.setTimestamp(report.getTimestamp());

        if (report.getDimensions() != null) {
            reportDO.setPitchScore(report.getDimensions().getPitch());
            reportDO.setRhythmScore(report.getDimensions().getRhythm());
            reportDO.setBreathScore(report.getDimensions().getBreath());
            reportDO.setVoiceScore(report.getDimensions().getVoice());
        }

        if (report.getSuggestions() != null) {
            reportDO.setSuggestions(JSON.toJSONString(report.getSuggestions()));
        }

        if (report.getTrainingRecommendations() != null) {
            reportDO.setTrainingRecommendations(JSON.toJSONString(report.getTrainingRecommendations()));
        }

        return reportDO;
    }
}
