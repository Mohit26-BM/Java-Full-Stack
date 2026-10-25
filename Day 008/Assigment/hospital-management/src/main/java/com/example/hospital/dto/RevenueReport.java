package com.example.hospital.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Revenue from COMPLETED appointments between two dates (inclusive). */
public record RevenueReport(LocalDate fromDate,
                            LocalDate toDate,
                            long completedAppointments,
                            BigDecimal totalRevenue) {
}
