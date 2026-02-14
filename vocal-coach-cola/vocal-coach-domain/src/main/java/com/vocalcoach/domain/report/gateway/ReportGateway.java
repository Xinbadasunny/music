package com.vocalcoach.domain.report.gateway;

import com.vocalcoach.domain.report.Report;
import java.util.List;
import java.util.Optional;

public interface ReportGateway {

    Report save(Report report);

    List<Report> findAll();

    Optional<Report> findById(Long id);

    void deleteById(Long id);

    boolean existsById(Long id);

    long count();

    Double getAverageScore();

    Integer getBestScore();

    Integer getWorstScore();

    Double getAveragePitchScore();

    Double getAverageRhythmScore();

    Double getAverageBreathScore();

    Double getAverageVoiceScore();

    List<Report> findTop10ByOrderByTimestampDesc();
}
