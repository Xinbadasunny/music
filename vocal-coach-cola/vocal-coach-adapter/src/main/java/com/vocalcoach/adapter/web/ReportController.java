package com.vocalcoach.adapter.web;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import com.vocalcoach.client.api.ReportServiceI;
import com.vocalcoach.client.dto.ReportDTO;
import com.vocalcoach.client.dto.StatisticsDTO;
import com.vocalcoach.client.dto.cmd.SaveReportCmd;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Resource
    private ReportServiceI reportService;

    @PostMapping
    public SingleResponse<ReportDTO> saveReport(@Valid @RequestBody SaveReportCmd cmd) {
        return reportService.saveReport(cmd);
    }

    @GetMapping
    public MultiResponse<ReportDTO> listReports() {
        return reportService.listReports();
    }

    @GetMapping("/{id}")
    public SingleResponse<ReportDTO> getReportById(@PathVariable Long id) {
        return reportService.getReportById(id);
    }

    @DeleteMapping("/{id}")
    public Response deleteReport(@PathVariable Long id) {
        return reportService.deleteReport(id);
    }

    @GetMapping("/statistics")
    public SingleResponse<StatisticsDTO> getStatistics() {
        return reportService.getStatistics();
    }
}
