package com.vocalcoach.client.api;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.vocalcoach.client.dto.AudioAnalysisDTO;
import com.vocalcoach.client.dto.EvaluationResultDTO;
import com.vocalcoach.client.dto.cmd.AnalyzeAudioCmd;

public interface EvaluationServiceI {

    SingleResponse<AudioAnalysisDTO> analyzeAudio(String audioFilePath, String referenceAudioPath);

    SingleResponse<EvaluationResultDTO> evaluate(AnalyzeAudioCmd cmd);

    MultiResponse<EvaluationResultDTO> listEvaluations();

    SingleResponse<EvaluationResultDTO> getEvaluation(Long id);
}
