package com.company.boot.dto;

import java.math.BigDecimal;

/**
 * Body of POST /api/transfers
 *
 * mode            - which TransferService method to use (see TransferMode)
 * simulateFailure - true = throw an exception AFTER the debit, BEFORE the credit
 */
public record TransferRequest(Long fromAccountId,
                              Long toAccountId,
                              BigDecimal amount,
                              TransferMode mode,
                              boolean simulateFailure) {
}
