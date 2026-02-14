package com.vocalcoach.domain.evaluation.gateway;

import com.vocalcoach.domain.evaluation.Evaluation;
import java.util.List;
import java.util.Optional;

public interface EvaluationGateway {

    Evaluation save(Evaluation evaluation);

    List<Evaluation> findAll();

    Optional<Evaluation> findById(Long id);

    void deleteById(Long id);

    long count();
}
