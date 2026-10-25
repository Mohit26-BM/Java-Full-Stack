package com.example.hospital.dto;

import java.math.BigDecimal;

/**
 * A lightweight doctor view, filled DIRECTLY by a JPQL query:
 *   SELECT new com.example.hospital.dto.DoctorSummary(d.id, d.name, d.specialization, d.consultationFee) ...
 * Only these four columns are read; no Doctor entity is loaded.
 */
public record DoctorSummary(Long id, String name, String specialization, BigDecimal consultationFee) {
}
