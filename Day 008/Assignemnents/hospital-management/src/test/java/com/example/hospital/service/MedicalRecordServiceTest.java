package com.example.hospital.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.hospital.entity.Appointment;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.entity.Department;
import com.example.hospital.entity.Doctor;
import com.example.hospital.entity.Gender;
import com.example.hospital.entity.MedicalRecord;
import com.example.hospital.entity.Patient;
import com.example.hospital.exception.InvalidAppointmentStatusException;
import com.example.hospital.exception.InvalidMedicalRecordException;
import com.example.hospital.exception.MedicalRecordAlreadyExistsException;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.MedicalRecordRepository;

/** Unit tests: the medical-record rules. */
@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private MedicalRecordService medicalRecordService;

    private Appointment appointment;
    private final LocalDate visitDate = LocalDate.of(2026, 10, 20);

    @BeforeEach
    void setUp() {
        Patient patient = new Patient("P1001", "Rajesh Kumar", LocalDate.of(1985, 4, 12), Gender.MALE,
                null, "9000000001", null);
        Doctor doctor = new Doctor("D001", "Dr. Anil Kumar", "Cardiology", "anil@hospital.com", "9876500001",
                new BigDecimal("800.00"), new Department("Cardiology", null));
        appointment = new Appointment("APT-TEST0001", patient, doctor, visitDate, LocalTime.of(10, 0),
                LocalTime.of(10, 30), null, new BigDecimal("800.00"));
        ReflectionTestUtils.setField(appointment, "id", 10L);
        appointment.setStatus(AppointmentStatus.COMPLETED);
    }

    @Test
    void recordForCompletedAppointment_isSaved() {
        when(medicalRecordRepository.existsByAppointmentId(10L)).thenReturn(false);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MedicalRecord record = medicalRecordService.createRecord(appointment, "Mild angina", "Aspirin 75mg",
                "Review ECG", visitDate.plusDays(14));

        assertThat(record.getAppointment()).isSameAs(appointment);
        assertThat(record.getDiagnosis()).isEqualTo("Mild angina");
    }

    @Test
    void secondRecordForTheSameAppointment_isRejected() {
        when(medicalRecordRepository.existsByAppointmentId(10L)).thenReturn(true);

        assertThatThrownBy(() -> medicalRecordService.createRecord(appointment, "Again", null, null, null))
                .isInstanceOf(MedicalRecordAlreadyExistsException.class)
                .hasMessage("Appointment APT-TEST0001 already has a medical record");
        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void recordForAppointmentThatIsNotCompleted_isRejected() {
        appointment.setStatus(AppointmentStatus.CONFIRMED);

        assertThatThrownBy(() -> medicalRecordService.createRecord(appointment, "Mild angina", null, null, null))
                .isInstanceOf(InvalidAppointmentStatusException.class);
    }

    @Test
    void followUpDateNotAfterTheVisit_isRejected() {
        when(medicalRecordRepository.existsByAppointmentId(10L)).thenReturn(false);

        assertThatThrownBy(() -> medicalRecordService.createRecord(appointment, "Mild angina", null, null,
                visitDate.minusDays(1)))
                .isInstanceOf(InvalidMedicalRecordException.class);
    }
}
