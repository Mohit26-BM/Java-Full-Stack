package com.example.hospital.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.example.hospital.dto.NoShowRunResponse;
import com.example.hospital.entity.Appointment;

/**
 * Unit test of the job's loop: each appointment is handled separately, so one
 * that a receptionist changed at the same moment (optimistic lock failure) is
 * skipped and the others are still marked.
 */
@ExtendWith(MockitoExtension.class)
class MissedAppointmentJobTest {

    @Mock
    private AppointmentService appointmentService;

    @Test
    void anAppointmentChangedConcurrentlyIsSkipped_andTheRestAreStillMarked() {
        MissedAppointmentJob job = new MissedAppointmentJob(appointmentService, Clock.systemDefaultZone(),
                Duration.ofHours(2));
        LocalDateTime now = LocalDateTime.of(2026, 11, 2, 18, 0);
        LocalDateTime cutoff = now.minusHours(2);

        when(appointmentService.findOpenAppointmentIdsEndedBefore(cutoff)).thenReturn(List.of(1L, 2L, 3L));
        when(appointmentService.markNoShowIfStillOpen(eq(1L), any(), anyString())).thenReturn(Optional.of("APT-1"));
        when(appointmentService.markNoShowIfStillOpen(eq(2L), any(), anyString()))
                .thenThrow(new ObjectOptimisticLockingFailureException(Appointment.class, 2L));
        when(appointmentService.markNoShowIfStillOpen(eq(3L), any(), anyString())).thenReturn(Optional.of("APT-3"));

        NoShowRunResponse result = job.run(now);

        assertThat(result.found()).isEqualTo(3);
        assertThat(result.markedNoShow()).isEqualTo(2);
        assertThat(result.skipped()).isEqualTo(1);
        assertThat(result.appointmentNumbers()).containsExactly("APT-1", "APT-3");
        assertThat(result.cutoff()).isEqualTo(cutoff);
    }
}
