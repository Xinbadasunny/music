package com.vocalcoach.client.api;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.SingleResponse;
import com.vocalcoach.client.dto.CourseDTO;
import com.vocalcoach.client.dto.TrainingProgressDTO;
import com.vocalcoach.client.dto.cmd.SaveProgressCmd;

import java.util.Map;

public interface TrainingServiceI {

    MultiResponse<CourseDTO> listCourses();

    SingleResponse<Map<String, Map<String, TrainingProgressDTO>>> getProgress();

    SingleResponse<TrainingProgressDTO> saveProgress(SaveProgressCmd cmd);

    SingleResponse<Integer> getOverallProgress();

    SingleResponse<Integer> getCompletedCount();
}
