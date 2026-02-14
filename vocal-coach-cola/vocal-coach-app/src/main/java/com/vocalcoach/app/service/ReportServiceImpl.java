package com.vocalcoach.app.service;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.vocalcoach.app.assembler.ReportAssembler;
import com.vocalcoach.client.api.ReportServiceI;
import com.vocalcoach.client.dto.ReportDTO;
import com.vocalcoach.client.dto.StatisticsDTO;
import com.vocalcoach.client.dto.cmd.SaveReportCmd;
import com.vocalcoach.domain.report.Report;
import com.vocalcoach.domain.report.gateway.ReportGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportServiceI {

    @Resource
    private ReportGateway reportGateway;

    @Resource
    private ReportAssembler reportAssembler;

    @Override
    @Transactional
    public SingleResponse<ReportDTO> saveReport(SaveReportCmd cmd) {
        Report report = reportAssembler.toEntity(cmd);
        report.setTimestamp(LocalDateTime.now());
        Report savedReport = reportGateway.save(report);
        return SingleResponse.of(reportAssembler.toDTO(savedReport));
    }

    @Override
    public MultiResponse<ReportDTO> listReports() {
        List<Report> reports = reportGateway.findAll();
        List<ReportDTO> dtoList = reports.stream()
                .map(reportAssembler::toDTO)
                .collect(Collectors.toList());
        return MultiResponse.of(dtoList);
    }

    @Override
    public SingleResponse<ReportDTO> getReportById(Long id) {
        return reportGateway.findById(id)
                .map(report -> SingleResponse.of(reportAssembler.toDTO(report)))
                .orElse(SingleResponse.buildFailure("REPORT_NOT_FOUND", "报告不存在"));
    }

    @Override
    @Transactional
    public Response deleteReport(Long id) {
        if (reportGateway.existsById(id)) {
            reportGateway.deleteById(id);
            return Response.buildSuccess();
        }
        return Response.buildFailure("REPORT_NOT_FOUND", "报告不存在");
    }

    @Override
    public SingleResponse<StatisticsDTO> getStatistics() {
        StatisticsDTO stats = new StatisticsDTO();

        long totalReports = reportGateway.count();
        stats.setTotalReports((int) totalReports);

        if (totalReports == 0) {
            stats.setAverageScore(0);
            stats.setBestScore(0);
            stats.setWorstScore(0);
            stats.setAveragePitch(0);
            stats.setAverageRhythm(0);
            stats.setAverageBreath(0);
            stats.setAverageVoice(0);
            stats.setImprovementTrend(0);
            stats.setRecentTrend("stable");
            return SingleResponse.of(stats);
        }

        Double avgScore = reportGateway.getAverageScore();
        stats.setAverageScore(avgScore != null ? avgScore.intValue() : 0);

        Integer bestScore = reportGateway.getBestScore();
        stats.setBestScore(bestScore != null ? bestScore : 0);

        Integer worstScore = reportGateway.getWorstScore();
        stats.setWorstScore(worstScore != null ? worstScore : 0);

        Double avgPitch = reportGateway.getAveragePitchScore();
        stats.setAveragePitch(avgPitch != null ? avgPitch.intValue() : 0);

        Double avgRhythm = reportGateway.getAverageRhythmScore();
        stats.setAverageRhythm(avgRhythm != null ? avgRhythm.intValue() : 0);

        Double avgBreath = reportGateway.getAverageBreathScore();
        stats.setAverageBreath(avgBreath != null ? avgBreath.intValue() : 0);

        Double avgVoice = reportGateway.getAverageVoiceScore();
        stats.setAverageVoice(avgVoice != null ? avgVoice.intValue() : 0);

        List<Report> recentReports = reportGateway.findTop10ByOrderByTimestampDesc();
        if (recentReports.size() >= 2) {
            int halfLength = recentReports.size() / 2;
            double oldAverage = 0;
            double newAverage = 0;

            for (int i = 0; i < halfLength; i++) {
                newAverage += recentReports.get(i).getOverallScore();
            }
            newAverage = newAverage / halfLength;

            for (int i = halfLength; i < recentReports.size(); i++) {
                oldAverage += recentReports.get(i).getOverallScore();
            }
            oldAverage = oldAverage / (recentReports.size() - halfLength);

            int trend = (int) (newAverage - oldAverage);
            stats.setImprovementTrend(trend);

            if (trend > 5) {
                stats.setRecentTrend("improving");
            } else if (trend < -5) {
                stats.setRecentTrend("declining");
            } else {
                stats.setRecentTrend("stable");
            }
        } else {
            stats.setImprovementTrend(0);
            stats.setRecentTrend("stable");
        }

        return SingleResponse.of(stats);
    }
}
