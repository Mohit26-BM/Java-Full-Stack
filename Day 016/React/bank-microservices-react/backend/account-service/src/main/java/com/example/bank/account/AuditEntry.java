package com.example.bank.account;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** One line of the audit trail: an administrative action on an account, and who did it. Insert-only. */
@Entity
@Table(name = "account_audit")
public class AuditEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String actor;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false, length = 40)
    private String action;

    private Long accountno;

    @Column(length = 300)
    private String detail;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected AuditEntry() {
    }

    public AuditEntry(String actor, String role, String action, Long accountno, String detail) {
        this.actor = actor;
        this.role = role == null ? "UNKNOWN" : role;
        this.action = action;
        this.accountno = accountno;
        this.detail = detail;
        this.createdAt = LocalDateTime.now().withNano(0);
    }

    public Long getId() {
        return id;
    }

    public String getActor() {
        return actor;
    }

    public String getRole() {
        return role;
    }

    public String getAction() {
        return action;
    }

    public Long getAccountno() {
        return accountno;
    }

    public String getDetail() {
        return detail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
