package com.example.bank.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(Long id, String reference, Long accountno, String type, BigDecimal amount,
                                  BigDecimal balanceAfter, Long relatedAccountno, String description,
                                  String performedBy, LocalDateTime createdAt) {

    static TransactionResponse from(AccountTransaction t) {
        return new TransactionResponse(t.getId(), t.getReference(), t.getAccountno(), t.getType().name(),
                t.getAmount(), t.getBalanceAfter(), t.getRelatedAccountno(), t.getDescription(),
                t.getPerformedBy(), t.getCreatedAt());
    }
}
