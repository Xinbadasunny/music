package com.vocalcoach.infrastructure.evaluation.gateway;

import com.alibaba.fastjson.JSON;
import com.vocalcoach.domain.evaluation.Evaluation;
import com.vocalcoach.domain.evaluation.gateway.EvaluationGateway;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class EvaluationGatewayImpl implements EvaluationGateway {

    private static final String DATA_DIR = "data";
    private static final String EVALUATIONS_FILE = "evaluations.json";

    private List<Evaluation> evaluationsCache = new ArrayList<>();
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
        File audiosDir = new File(DATA_DIR, "audios");
        if (!audiosDir.exists()) {
            audiosDir.mkdirs();
        }
    }

    private void loadFromFile() {
        File file = new File(DATA_DIR, EVALUATIONS_FILE);
        if (file.exists()) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                evaluationsCache = JSON.parseArray(content, Evaluation.class);
                if (evaluationsCache == null) {
                    evaluationsCache = new ArrayList<>();
                }
                long maxId = evaluationsCache.stream()
                        .mapToLong(e -> e.getId() != null ? e.getId() : 0)
                        .max()
                        .orElse(0);
                idGenerator.set(maxId + 1);
            } catch (IOException e) {
                evaluationsCache = new ArrayList<>();
            }
        }
    }

    private void saveToFile() {
        try {
            String content = JSON.toJSONString(evaluationsCache);
            Files.write(Paths.get(DATA_DIR, EVALUATIONS_FILE), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Evaluation save(Evaluation evaluation) {
        if (evaluation.getId() == null) {
            evaluation.setId(idGenerator.getAndIncrement());
            evaluationsCache.add(evaluation);
        } else {
            evaluationsCache.removeIf(e -> evaluation.getId().equals(e.getId()));
            evaluationsCache.add(evaluation);
        }
        saveToFile();
        return evaluation;
    }

    @Override
    public List<Evaluation> findAll() {
        return new ArrayList<>(evaluationsCache);
    }

    @Override
    public Optional<Evaluation> findById(Long id) {
        return evaluationsCache.stream()
                .filter(e -> id.equals(e.getId()))
                .findFirst();
    }

    @Override
    public void deleteById(Long id) {
        evaluationsCache.removeIf(e -> id.equals(e.getId()));
        saveToFile();
    }

    @Override
    public long count() {
        return evaluationsCache.size();
    }
}
