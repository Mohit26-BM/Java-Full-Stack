package com.example.hospital.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.CompleteAppointmentRequest;
import com.example.hospital.dto.NoShowRunResponse;
import com.example.hospital.dto.StatusChangeResponse;
import com.example.hospital.dto.UpdateStatusRequest;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.service.MissedAppointmentJob;

/**
 * Extension: the NO_SHOW job. The job is run "at" chosen moments (the grace
 * period is 2 hours in the test properties), so the appointments can be booked
 * in the future as the booking rules require.
 */
class MissedAppointmentJobIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MissedAppointmentJob job;

    private Long doctorId;
    private Long patientId;

    @BeforeEach
    void createReferenceData() {
        Long departmentId = createDepartment("Cardiology").id();
        doctorId = createDoctor("D001", departmentId, "800.00").id();
        patientId = createPatient("P1001").id();
    }

    private LocalDateTime at(String time) {
        return LocalDateTime.of(visitDate, LocalTime.parse(time));
    }

    /** Appointment ends 10:30; with a 2-hour grace period it becomes a no-show at 12:30, not before. */
    @Test
    void anOpenAppointmentBecomesNoShowExactlyWhenTheGracePeriodEnds() {
        AppointmentResponse booked = book(patientId, doctorId, visitDate, "10:00", "10:30");

        NoShowRunResponse tooEarly = job.run(at("12:29"));
        assertThat(tooEarly.markedNoShow()).isZero();
        assertThat(appointmentService.findById(booked.id()).status()).isEqualTo(AppointmentStatus.BOOKED);

        NoShowRunResponse onTime = job.run(at("12:30"));
        assertThat(onTime.markedNoShow()).isEqualTo(1);
        assertThat(onTime.appointmentNumbers()).containsExactly(booked.appointmentNumber());
        assertThat(appointmentService.findById(booked.id()).status()).isEqualTo(AppointmentStatus.NO_SHOW);
    }

    @Test
    void confirmedAppointmentsAreMarkedToo_butCompletedAndCancelledAreLeftAlone() {
        AppointmentResponse booked = book(patientId, doctorId, visitDate, "09:00", "09:30");
        AppointmentResponse toConfirm = book(patientId, doctorId, visitDate, "09:30", "10:00");
        AppointmentResponse toCancel = book(patientId, doctorId, visitDate, "10:00", "10:30");
        AppointmentResponse toComplete = book(patientId, doctorId, visitDate, "10:30", "11:00");

        appointmentService.changeStatus(toConfirm.id(), new UpdateStatusRequest(AppointmentStatus.CONFIRMED, 0L));
        appointmentService.changeStatus(toCancel.id(), new UpdateStatusRequest(AppointmentStatus.CANCELLED, 0L));
        AppointmentResponse confirmed = appointmentService.changeStatus(toComplete.id(),
                new UpdateStatusRequest(AppointmentStatus.CONFIRMED, 0L));
        appointmentService.completeAppointment(toComplete.id(),
                new CompleteAppointmentRequest(confirmed.version(), "Healthy", null, null, null));

        NoShowRunResponse result = job.run(at("18:00"));

        assertThat(result.found()).isEqualTo(2);
        assertThat(result.appointmentNumbers()).containsExactly(booked.appointmentNumber(), toConfirm.appointmentNumber());
        assertThat(appointmentService.findById(toCancel.id()).status()).isEqualTo(AppointmentStatus.CANCELLED);
        assertThat(appointmentService.findById(toComplete.id()).status()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    void theJobWritesTheAuditRow_andASecondRunChangesNothing() {
        AppointmentResponse booked = book(patientId, doctorId, visitDate, "10:00", "10:30");

        job.run(at("18:00"));
        NoShowRunResponse secondRun = job.run(at("19:00"));

        assertThat(secondRun.found()).isZero();
        StatusChangeResponse last = appointmentService.getStatusHistory(booked.id()).get(1);
        assertThat(last.fromStatus()).isEqualTo(AppointmentStatus.BOOKED);
        assertThat(last.toStatus()).isEqualTo(AppointmentStatus.NO_SHOW);
        assertThat(last.changedBy()).isEqualTo("NO_SHOW_JOB");
        assertThat(last.note()).isEqualTo("Not completed within 2 hour(s) after the end time");
    }
}
