package com.example.bank.account;

import java.time.LocalDateTime;

public record AuditResponse(Long id, String actor, String role, String action, Long accountno, String detail,
                            LocalDateTime createdAt) {

    static AuditResponse from(AuditEntry e) {
        return new AuditResponse(e.getId(), e.getActor(), e.getRole(), e.getAction(), e.getAccountno(), e.getDetail(),
                e.getCreatedAt());
    }
}
