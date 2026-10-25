package com.example.hospital.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.UpdateStatusRequest;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.exception.DoctorUnavailableException;

/**
 * The time-boundary rule, tested against the real overlap query in MySQL.
 *
 * Rule: slots are HALF-OPEN [start, end). Two slots overlap when
 *       existing.start < new.end AND existing.end > new.start.
 * Existing appointment in every test: 10:00-10:30.
 */
class AppointmentOverlapIntegrationTest extends IntegrationTestBase {

    private Long doctorId;
    private Long otherDoctorId;
    private Long patientId;
    private AppointmentResponse existing;

    @BeforeEach
    void bookTheExistingAppointment() {
        Long departmentId = createDepartment("Cardiology").id();
        doctorId = createDoctor("D001", departmentId, "800.00").id();
        otherDoctorId = createDoctor("D002", departmentId, "700.00").id();
        patientId = createPatient("P1001").id();
        Long otherPatientId = createPatient("P1002").id();
        existing = book(otherPatientId, doctorId, visitDate, "10:00", "10:30");
    }

    @ParameterizedTest(name = "{0}-{1} -> {2}")
    @CsvSource({
            // new start, new end, result
            "10:15, 10:45, REJECTED",   // starts inside the existing slot
            "09:45, 10:15, REJECTED",   // ends inside the existing slot
            "10:05, 10:20, REJECTED",   // completely inside
            "09:30, 11:00, REJECTED",   // completely covers it
            "10:00, 10:30, REJECTED",   // exactly the same slot
            "10:30, 11:00, ALLOWED",    // starts exactly when the existing one ends
            "09:30, 10:00, ALLOWED",    // ends exactly when the existing one starts
            "11:00, 11:30, ALLOWED"     // later, no contact at all
    })
    void overlapRule(String start, String end, String expected) {
        if (expected.equals("ALLOWED")) {
            AppointmentResponse booked = book(patientId, doctorId, visitDate, start, end);
            assertThat(booked.status()).isEqualTo(AppointmentStatus.BOOKED);
        } else {
            assertThatThrownBy(() -> book(patientId, doctorId, visitDate, start, end))
                    .isInstanceOf(DoctorUnavailableException.class);
        }
    }

    @Test
    void sameTime_withADifferentDoctor_isAllowed() {
        AppointmentResponse booked = book(patientId, otherDoctorId, visitDate, "10:00", "10:30");
        assertThat(booked.status()).isEqualTo(AppointmentStatus.BOOKED);
    }

    @Test
    void sameTime_onAnotherDate_isAllowed() {
        AppointmentResponse booked = book(patientId, doctorId, visitDate.plusDays(1), "10:00", "10:30");
        assertThat(booked.status()).isEqualTo(AppointmentStatus.BOOKED);
    }

    @Test
    void cancelledAppointment_freesTheSlot() {
        appointmentService.changeStatus(existing.id(), new UpdateStatusRequest(AppointmentStatus.CANCELLED, 0L));

        AppointmentResponse booked = book(patientId, doctorId, visitDate, "10:00", "10:30");
        assertThat(booked.status()).isEqualTo(AppointmentStatus.BOOKED);
    }
}
