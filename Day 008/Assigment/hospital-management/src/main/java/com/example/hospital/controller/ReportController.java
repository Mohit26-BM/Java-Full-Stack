package com.example.hospital.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hospital.dto.DoctorWorkload;
import com.example.hospital.dto.RevenueReport;
import com.example.hospital.service.ReportService;

import io.swagger.v3.oas.annotations.tags.Tag;

/** Reports - /api/reports */
@Tag(name = "Reports", description = "Department workload and revenue reports")
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /** Query 3: GET /api/reports/department-workload?departmentId=1&fromDate=2026-10-01&toDate=2026-10-31 */
    @GetMapping("/department-workload")
    public List<DoctorWorkload> departmentWorkload(@RequestParam("departmentId") Long departmentId,
                                                   @RequestParam("fromDate") LocalDate fromDate,
                                                   @RequestParam("toDate") LocalDate toDate) {
        return reportService.getDepartmentWorkload(departmentId, fromDate, toDate);
    }

    /** Query 4: GET /api/reports/revenue?fromDate=2026-10-01&toDate=2026-10-31 */
    @GetMapping("/revenue")
    public RevenueReport revenue(@RequestParam("fromDate") LocalDate fromDate,
                                 @RequestParam("toDate") LocalDate toDate) {
        return reportService.getRevenue(fromDate, toDate);
    }
}
