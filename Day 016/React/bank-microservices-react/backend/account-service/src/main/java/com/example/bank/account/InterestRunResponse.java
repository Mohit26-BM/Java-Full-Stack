package com.example.bank.account;

import java.math.BigDecimal;

/** Result of an interest run for one month. A repeat run credits nothing (accountsCredited 0). */
public record InterestRunResponse(String month, int accountsCredited, BigDecimal totalInterest) {
}
