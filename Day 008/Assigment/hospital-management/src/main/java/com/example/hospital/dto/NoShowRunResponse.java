package com.example.hospital.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Result of one run of the missed-appointment job.
 * cutoff: appointments that ended at or before this moment were checked.
 * skipped: changed by someone else during the run (optimistic lock) - left as they were.
 */
public record NoShowRunResponse(LocalDateTime ranAt,
                                LocalDateTime cutoff,
                                int found,
                                int markedNoShow,
                                int skipped,
                                List<String> appointmentNumbers) {
}
