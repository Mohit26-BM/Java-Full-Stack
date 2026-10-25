package com.example.hospital.dto;

import java.math.BigDecimal;

public record DoctorResponse(Long id,
                             String doctorCode,
                             String name,
                             String specialization,
                             String email,
                             String phone,
                             BigDecimal consultationFee,
                             Boolean active,
                             Long departmentId,
                             String departmentName) {
}
