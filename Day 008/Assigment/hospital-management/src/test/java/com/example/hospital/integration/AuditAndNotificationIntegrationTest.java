package com.example.hospital.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.CompleteAppointmentRequest;
import com.example.hospital.dto.NotificationResponse;
import com.example.hospital.dto.StatusChangeResponse;
import com.example.hospital.dto.UpdateStatusRequest;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.exception.InvalidMedicalRecordException;

/** Extension: the status-change audit table and the after-commit notifications. */
class AuditAndNotificationIntegrationTest extends IntegrationTestBase {

    @Autowired
    private PlatformTransactionManager transactionManager;

    private Long doctorId;
    private Long patientId;

    @BeforeEach
    void createReferenceData() {
        Long departmentId = createDepartment("Cardiology").id();
        doctorId = createDoctor("D001", departmentId, "800.00").id();
        patientId = createPatient("P1001").id();
    }

    @Test
    void everyStatusChangeIsRecorded_oldestFirst() {
        AppointmentResponse booked = book(patientId, doctorId, visitDate, "10:00", "10:30");
        AppointmentResponse confirmed = appointmentService.changeStatus(booked.id(),
                new UpdateStatusRequest(AppointmentStatus.CONFIRMED, booked.version()));
        appointmentService.changeStatus(booked.id(), new UpdateStatusRequest(AppointmentStatus.CANCELLED, confirmed.version()));

        List<StatusChangeResponse> history = appointmentService.getStatusHistory(booked.id());

        assertThat(history).extracting(StatusChangeResponse::fromStatus)
                .containsExactly(null, AppointmentStatus.BOOKED, AppointmentStatus.CONFIRMED);
        assertThat(history).extracting(StatusChangeResponse::toStatus)
                .containsExactly(AppointmentStatus.BOOKED, AppointmentStatus.CONFIRMED, AppointmentStatus.CANCELLED);
        assertThat(history).allMatch(change -> change.changedBy().equals("API") && change.changedAt() != null);
    }

    /** The audit row is written in the completion transaction, so the rollback removes it as well. */
    @Test
    void rolledBackCompletion_leavesNoAuditRow() {
        AppointmentResponse booked = book(patientId, doctorId, visitDate, "10:00", "10:30");
        AppointmentResponse confirmed = appointmentService.changeStatus(booked.id(),
                new UpdateStatusRequest(AppointmentStatus.CONFIRMED, booked.version()));

        assertThatThrownBy(() -> appointmentService.completeAppointment(booked.id(), new CompleteAppointmentRequest(
                confirmed.version(), "Migraine", null, null, visitDate.minusDays(1))))   // follow-up before the visit
                .isInstanceOf(InvalidMedicalRecordException.class);

        assertThat(appointmentService.getStatusHistory(booked.id()))
                .extracting(StatusChangeResponse::toStatus)
                .containsExactly(AppointmentStatus.BOOKED, AppointmentStatus.CONFIRMED);   // no COMPLETED row
    }

    @Test
    void bookingAndCancellation_sendNotificationsAfterCommit() {
        AppointmentResponse booked = book(patientId, doctorId, visitDate, "10:00", "10:30");
        appointmentService.changeStatus(booked.id(), new UpdateStatusRequest(AppointmentStatus.CANCELLED, booked.version()));

        List<NotificationResponse> sent = notificationService.recentNotifications();

        assertThat(sent).extracting(NotificationResponse::type).containsExactly("CANCELLATION", "BOOKING_CONFIRMATION");
        assertThat(sent).allMatch(n -> n.appointmentNumber().equals(booked.appointmentNumber()));
        // the test patient has no e-mail address, so the notification goes by SMS to the phone number
        assertThat(sent.get(1).recipient()).isEqualTo("SMS 9000000001");
        assertThat(sent.get(1).message()).contains("is booked for " + visitDate + " at 10:00");
    }

    @Test
    void confirmingDoesNotNotify() {
        AppointmentResponse booked = book(patientId, doctorId, visitDate, "10:00", "10:30");
        notificationService.clear();

        appointmentService.changeStatus(booked.id(), new UpdateStatusRequest(AppointmentStatus.CONFIRMED, booked.version()));

        assertThat(notificationService.recentNotifications()).isEmpty();
    }

    /**
     * The booking runs inside an outer transaction that is then rolled back.
     * The event WAS published, but @TransactionalEventListener(AFTER_COMMIT)
     * never runs, so no notification is sent - and no appointment exists.
     */
    @Test
    void rolledBackBooking_sendsNoNotification() {
        TransactionTemplate outer = new TransactionTemplate(transactionManager);

        outer.executeWithoutResult(status -> {
            book(patientId, doctorId, visitDate, "10:00", "10:30");   // joins the outer transaction
            status.setRollbackOnly();
        });

        assertThat(appointmentRepository.count()).isZero();
        assertThat(statusChangeRepository.count()).isZero();
        assertThat(notificationService.recentNotifications()).isEmpty();
    }
}
