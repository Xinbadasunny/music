package com.vocalcoach.infrastructure.report.repository;

import com.vocalcoach.infrastructure.report.dataobject.ReportDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportJpaRepository extends JpaRepository<ReportDO, Long> {

    @Query("SELECT AVG(r.overallScore) FROM ReportDO r")
    Double getAverageScore();

    @Query("SELECT MAX(r.overallScore) FROM ReportDO r")
    Integer getBestScore();

    @Query("SELECT MIN(r.overallScore) FROM ReportDO r")
    Integer getWorstScore();

    @Query("SELECT AVG(r.pitchScore) FROM ReportDO r")
    Double getAveragePitchScore();

    @Query("SELECT AVG(r.rhythmScore) FROM ReportDO r")
    Double getAverageRhythmScore();

    @Query("SELECT AVG(r.breathScore) FROM ReportDO r")
    Double getAverageBreathScore();

    @Query("SELECT AVG(r.voiceScore) FROM ReportDO r")
    Double getAverageVoiceScore();

    List<ReportDO> findTop10ByOrderByTimestampDesc();
}
