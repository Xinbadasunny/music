package com.vocalcoach.infrastructure.report.gateway;

import com.alibaba.fastjson.JSON;
import com.vocalcoach.domain.report.Report;
import com.vocalcoach.domain.report.gateway.ReportGateway;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class ReportGatewayImpl implements ReportGateway {

    private static final String DATA_DIR = "data";
    private static final String REPORTS_FILE = "reports.json";

    private List<Report> reportsCache = new ArrayList<>();
    private AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void init() {
        ensureDataDir();
        loadFromFile();
    }

    private void ensureDataDir() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private void loadFromFile() {
        File file = new File(DATA_DIR, REPORTS_FILE);
        if (file.exists()) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                reportsCache = JSON.parseArray(content, Report.class);
                if (reportsCache == null) {
                    reportsCache = new ArrayList<>();
                }
                long maxId = reportsCache.stream().mapToLong(r -> r.getId() != null ? r.getId() : 0).max().orElse(0);
                idGenerator.set(maxId + 1);
            } catch (IOException e) {
                reportsCache = new ArrayList<>();
            }
        }
    }

    private void saveToFile() {
        try {
            String content = JSON.toJSONString(reportsCache);
            Files.write(Paths.get(DATA_DIR, REPORTS_FILE), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Report save(Report report) {
        if (report.getId() == null) {
            report.setId(idGenerator.getAndIncrement());
            reportsCache.add(report);
        } else {
            reportsCache.removeIf(r -> report.getId().equals(r.getId()));
            reportsCache.add(report);
        }
        saveToFile();
        return report;
    }

    @Override
    public List<Report> findAll() {
        return new ArrayList<>(reportsCache);
    }

    @Override
    public Optional<Report> findById(Long id) {
        return reportsCache.stream()
                .filter(r -> id.equals(r.getId()))
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        reportsCache.removeIf(r -> id.equals(r.getId()));
        saveToFile();
    }

    @Override
    public boolean existsById(Long id) {
        return reportsCache.stream().anyMatch(r -> id.equals(r.getId()));
    }

    @Override
    public long count() {
        return reportsCache.size();
    }

    @Override
    public Double getAverageScore() {
        if (reportsCache.isEmpty()) return null;
        return reportsCache.stream()
                .mapToInt(r -> r.getOverallScore() != null ? r.getOverallScore() : 0)
                .average()
                .orElse(0);
    }

    @Override
    public Integer getBestScore() {
        return reportsCache.stream()
                .mapToInt(r -> r.getOverallScore() != null ? r.getOverallScore() : 0)
                .max()
                .orElse(0);
    }

    @Override
    public Integer getWorstScore() {
        return reportsCache.stream()
                .mapToInt(r -> r.getOverallScore() != null ? r.getOverallScore() : 0)
                .min()
                .orElse(0);
    }

    @Override
    public Double getAveragePitchScore() {
        if (reportsCache.isEmpty()) return null;
        return reportsCache.stream()
                .filter(r -> r.getDimensions() != null && r.getDimensions().getPitch() != null)
                .mapToInt(r -> r.getDimensions().getPitch())
                .average()
                .orElse(0);
    }

    @Override
    public Double getAverageRhythmScore() {
        if (reportsCache.isEmpty()) return null;
        return reportsCache.stream()
                .filter(r -> r.getDimensions() != null && r.getDimensions().getRhythm() != null)
                .mapToInt(r -> r.getDimensions().getRhythm())
                .average()
                .orElse(0);
    }

    @Override
    public Double getAverageBreathScore() {
        if (reportsCache.isEmpty()) return null;
        return reportsCache.stream()
                .filter(r -> r.getDimensions() != null && r.getDimensions().getBreath() != null)
                .mapToInt(r -> r.getDimensions().getBreath())
                .average()
                .orElse(0);
    }

    @Override
    public Double getAverageVoiceScore() {
        if (reportsCache.isEmpty()) return null;
        return reportsCache.stream()
                .filter(r -> r.getDimensions() != null && r.getDimensions().getVoice() != null)
                .mapToInt(r -> r.getDimensions().getVoice())
                .average()
                .orElse(0);
    }

    @Override
    public List<Report> findTop10ByOrderByTimestampDesc() {
        return reportsCache.stream()
                .sorted(Comparator.comparing(Report::getTimestamp, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .collect(Collectors.toList());
    }

    // ==================== 以下为数据库实现代码，待数据库就绪后启用 ====================
    // @Resource
    // private ReportJpaRepository reportRepository;
    //
    // @Override
    // public Report save(Report report) {
    //     ReportDO reportDO = toDO(report);
    //     ReportDO saved = reportRepository.save(reportDO);
    //     return toEntity(saved);
    // }
    //
    // @Override
    // public List<Report> findAll() {
    //     return reportRepository.findAll().stream()
    //             .map(this::toEntity)
    //             .collect(Collectors.toList());
    // }
    //
    // @Override
    // public Optional<Report> findById(Long id) {
    //     return reportRepository.findById(id).map(this::toEntity);
    // }
    //
    // @Override
    // public void deleteById(Long id) {
    //     reportRepository.deleteById(id);
    // }
    //
    // @Override
    // public boolean existsById(Long id) {
    //     return reportRepository.existsById(id);
    // }
    //
    // @Override
    // public long count() {
    //     return reportRepository.count();
    // }
    //
    // @Override
    // public Double getAverageScore() {
    //     return reportRepository.getAverageScore();
    // }
    //
    // @Override
    // public Integer getBestScore() {
    //     return reportRepository.getBestScore();
    // }
    //
    // @Override
    // public Integer getWorstScore() {
    //     return reportRepository.getWorstScore();
    // }
    //
    // @Override
    // public Double getAveragePitchScore() {
    //     return reportRepository.getAveragePitchScore();
    // }
    //
    // @Override
    // public Double getAverageRhythmScore() {
    //     return reportRepository.getAverageRhythmScore();
    // }
    //
    // @Override
    // public Double getAverageBreathScore() {
    //     return reportRepository.getAverageBreathScore();
    // }
    //
    // @Override
    // public Double getAverageVoiceScore() {
    //     return reportRepository.getAverageVoiceScore();
    // }
    //
    // @Override
    // public List<Report> findTop10ByOrderByTimestampDesc() {
    //     return reportRepository.findTop10ByOrderByTimestampDesc().stream()
    //             .map(this::toEntity)
    //             .collect(Collectors.toList());
    // }
    //
    // private Report toEntity(ReportDO reportDO) { ... }
    // private ReportDO toDO(Report report) { ... }
}
