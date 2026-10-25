package com.example.hospital.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hospital.dto.DoctorWorkload;
import com.example.hospital.dto.RevenueReport;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.exception.DepartmentNotFoundException;
import com.example.hospital.exception.InvalidSearchCriteriaException;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DepartmentRepository;

/** Reports: department workload (Query 3) and revenue (Query 4). */
@Service
@Transactional(readOnly = true)
public class ReportService {

    private final AppointmentRepository appointmentRepository;
    private final DepartmentRepository departmentRepository;

    public ReportService(AppointmentRepository appointmentRepository, DepartmentRepository departmentRepository) {
        this.appointmentRepository = appointmentRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<DoctorWorkload> getDepartmentWorkload(Long departmentId, LocalDate fromDate, LocalDate toDate) {
        checkDateRange(fromDate, toDate);
        if (!departmentRepository.existsById(departmentId)) {
            throw new DepartmentNotFoundException(departmentId);
        }
        return appointmentRepository.findDepartmentWorkload(departmentId, fromDate, toDate);
    }

    public RevenueReport getRevenue(LocalDate fromDate, LocalDate toDate) {
        checkDateRange(fromDate, toDate);
        long completed = appointmentRepository.countByStatusAndAppointmentDateBetween(
                AppointmentStatus.COMPLETED, fromDate, toDate);
        BigDecimal total = appointmentRepository.sumCompletedRevenue(fromDate, toDate);
        return new RevenueReport(fromDate, toDate, completed, total);
    }

    private void checkDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new InvalidSearchCriteriaException("fromDate must not be after toDate");
        }
    }
}
