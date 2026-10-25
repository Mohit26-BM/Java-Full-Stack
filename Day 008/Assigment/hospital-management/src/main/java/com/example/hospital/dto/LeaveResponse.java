package com.example.hospital.dto;

import java.time.LocalDate;

public record LeaveResponse(Long id,
                            Long doctorId,
                            LocalDate startDate,
                            LocalDate endDate,
                            String reason) {
}
