package com.example.hospital.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.CompleteAppointmentRequest;
import com.example.hospital.dto.MedicalRecordRequest;
import com.example.hospital.dto.PageResponse;
import com.example.hospital.dto.PatientHistoryItem;
import com.example.hospital.dto.UpdateStatusRequest;
import com.example.hospital.entity.Appointment;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.entity.Doctor;
import com.example.hospital.exception.InvalidMedicalRecordException;
import com.example.hospital.exception.MedicalRecordAlreadyExistsException;

/** End-to-end business flows against the real database. */
class AppointmentWorkflowIntegrationTest extends IntegrationTestBase {

    private Long departmentId;
    private Long doctorId;
    private Long patientId;

    @BeforeEach
    void createReferenceData() {
        departmentId = createDepartment("Cardiology").id();
        doctorId = createDoctor("D001", departmentId, "800.00").id();
        patientId = createPatient("P1001").id();
    }

    @Test
    void createPatient_isPersisted() {
        assertThat(patientRepository.findById(patientId)).isPresent();
        assertThat(patientRepository.findById(patientId).get().getPatientNumber()).isEqualTo("P1001");
    }

    @Test
    void createDoctorWithDepartment_persistsTheRelationship() {
        List<Doctor> doctors = doctorRepository.findByDepartmentIdAndActiveTrue(departmentId);

        assertThat(doctors).hasSize(1);
        assertThat(doctors.get(0).getDoctorCode()).isEqualTo("D001");
        assertThat(doctors.get(0).getDepartment().getName()).isEqualTo("Cardiology");
    }

    @Test
    void completeAppointment_changesStatus_createsRecord_andAppearsInHistory() {
        AppointmentResponse booked = book(patientId, doctorId, visitDate, "10:00", "10:30");
        AppointmentResponse confirmed = appointmentService.changeStatus(booked.id(),
                new UpdateStatusRequest(AppointmentStatus.CONFIRMED, booked.version()));

        var result = appointmentService.completeAppointment(booked.id(), new CompleteAppointmentRequest(
                confirmed.version(), "Mild angina", "Aspirin 75mg", "Review ECG", visitDate.plusDays(14)));

        assertThat(result.appointment().status()).isEqualTo(AppointmentStatus.COMPLETED);
        assertThat(medicalRecordRepository.existsByAppointmentId(booked.id())).isTrue();

        List<PatientHistoryItem> history = patientService.getMedicalHistory(patientId);
        assertThat(history).hasSize(1);
        assertThat(history.get(0).diagnosis()).isEqualTo("Mild angina");
        assertThat(history.get(0).fee()).isEqualByComparingTo("800.00");
    }

    /**
     * Section 17 - controlled failure: the medical record is invalid (follow-up
     * date before the visit). The status UPDATE was already sent to MySQL, but
     * the transaction rolls it back: the appointment is still CONFIRMED.
     */
    @Test
    void completeAppointment_whenRecordCreationFails_rollsBackTheStatusChange() {
        AppointmentResponse booked = book(patientId, doctorId, visitDate, "10:00", "10:30");
        AppointmentResponse confirmed = appointmentService.changeStatus(booked.id(),
                new UpdateStatusRequest(AppointmentStatus.CONFIRMED, booked.version()));

        assertThatThrownBy(() -> appointmentService.completeAppointment(booked.id(), new CompleteAppointmentRequest(
                confirmed.version(), "Mild angina", null, null, visitDate.minusDays(1))))
                .isInstanceOf(InvalidMedicalRecordException.class);

        Appointment reloaded = appointmentRepository.findById(booked.id()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(reloaded.getVersion()).isEqualTo(confirmed.version());     // the UPDATE did not survive
        assertThat(medicalRecordRepository.count()).isZero();
    }

    @Test
    void createMedicalRecordTwice_secondAttemptIsRejected() {
        AppointmentResponse booked = book(patientId, doctorId, visitDate, "10:00", "10:30");
        AppointmentResponse confirmed = appointmentService.changeStatus(booked.id(),
                new UpdateStatusRequest(AppointmentStatus.CONFIRMED, booked.version()));
        appointmentService.completeAppointment(booked.id(),
                new CompleteAppointmentRequest(confirmed.version(), "Mild angina", null, null, null));

        assertThatThrownBy(() -> medicalRecordService.createMedicalRecord(
                new MedicalRecordRequest(booked.id(), "Second diagnosis", null, null, null)))
                .isInstanceOf(MedicalRecordAlreadyExistsException.class);
        assertThat(medicalRecordRepository.count()).isEqualTo(1);
    }

    @Test
    void paginatedSearch_returnsTheRightPageAndMetadata() {
        // 12 appointments on 12 different days
        for (int day = 0; day < 12; day++) {
            book(patientId, doctorId, visitDate.plusDays(day), "10:00", "10:30");
        }

        PageResponse<AppointmentResponse> page = appointmentService.searchAppointments(doctorId, null, null,
                AppointmentStatus.BOOKED, null, PageRequest.of(1, 5, Sort.by(Sort.Direction.DESC, "appointmentDate")));

        assertThat(page.page()).isEqualTo(1);
        assertThat(page.size()).isEqualTo(5);
        assertThat(page.totalElements()).isEqualTo(12);
        assertThat(page.totalPages()).isEqualTo(3);
        assertThat(page.content()).hasSize(5);
        // page 1 (the second page) of newest-first: days 6, 5, 4, 3, 2
        assertThat(page.content().get(0).appointmentDate()).isEqualTo(visitDate.plusDays(6));
        assertThat(page.content().get(4).appointmentDate()).isEqualTo(visitDate.plusDays(2));
    }
}
