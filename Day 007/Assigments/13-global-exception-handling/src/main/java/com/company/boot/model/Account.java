package com.company.boot.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** One row of a13_accounts (also returned as JSON). */
public record Account(int accountId, String holderName, String email, BigDecimal balance, LocalDateTime createdAt) {
}
