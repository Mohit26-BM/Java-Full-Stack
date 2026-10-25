package com.company.secureapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.secureapi.entity.SalaryAudit;

public interface SalaryAuditRepository extends JpaRepository<SalaryAudit, Long> {
    List<SalaryAudit> findByEmployeeIdOrderByIdAsc(Long employeeId);
}
