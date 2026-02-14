package com.vocalcoach.client.api;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.vocalcoach.client.dto.ReportDTO;
import com.vocalcoach.client.dto.StatisticsDTO;
import com.vocalcoach.client.dto.cmd.SaveReportCmd;

public interface ReportServiceI {

    SingleResponse<ReportDTO> saveReport(SaveReportCmd cmd);

    MultiResponse<ReportDTO> listReports();

    SingleResponse<ReportDTO> getReportById(Long id);

    Response deleteReport(Long id);

    SingleResponse<StatisticsDTO> getStatistics();
}
