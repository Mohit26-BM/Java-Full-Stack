package com.company.secureapi.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "salary_audit")
public class SalaryAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(name = "old_salary", nullable = false, precision = 11, scale = 2)
    private BigDecimal oldSalary;

    @Column(name = "new_salary", nullable = false, precision = 11, scale = 2)
    private BigDecimal newSalary;

    @Column(name = "changed_by", nullable = false, length = 30)
    private String changedBy;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    protected SalaryAudit() {
    }

    public SalaryAudit(Long employeeId, BigDecimal oldSalary, BigDecimal newSalary, String changedBy, LocalDateTime changedAt) {
        this.employeeId = employeeId;
        this.oldSalary = oldSalary;
        this.newSalary = newSalary;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    public Long getEmployeeId() { return employeeId; }
    public BigDecimal getOldSalary() { return oldSalary; }
    public BigDecimal getNewSalary() { return newSalary; }
    public String getChangedBy() { return changedBy; }
    public LocalDateTime getChangedAt() { return changedAt; }
}
