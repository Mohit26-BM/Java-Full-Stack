package com.example.hospital.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.CompleteAppointmentRequest;
import com.example.hospital.dto.CreateAppointmentRequest;
import com.example.hospital.dto.UpdateStatusRequest;
import com.example.hospital.entity.Appointment;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.entity.Department;
import com.example.hospital.entity.Doctor;
import com.example.hospital.entity.Gender;
import com.example.hospital.entity.MedicalRecord;
import com.example.hospital.entity.Patient;
import com.example.hospital.exception.AppointmentModifiedException;
import com.example.hospital.exception.DoctorUnavailableException;
import com.example.hospital.exception.InvalidAppointmentStatusException;
import com.example.hospital.exception.InvalidAppointmentTimeException;
import com.example.hospital.exception.InvalidSearchCriteriaException;
import com.example.hospital.exception.PatientInactiveException;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.PatientRepository;

/**
 * Unit tests: the booking workflow and lifecycle rules of AppointmentService.
 * Repositories are Mockito mocks; ids and versions are set with
 * ReflectionTestUtils because these entities are never saved to a database.
 */
@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private MedicalRecordService medicalRecordService;

    @Mock
    private AppointmentNumberGenerator appointmentNumberGenerator;

    @InjectMocks
    private AppointmentService appointmentService;

    private Patient patient;
    private Doctor doctor;
    private final LocalDate nextWeek = LocalDate.now().plusDays(7);

    @BeforeEach
    void setUp() {
        patient = new Patient("P1001", "Rajesh Kumar", LocalDate.of(1985, 4, 12), Gender.MALE,
                "rajesh@example.com", "9000000001", "Bengaluru");
        ReflectionTestUtils.setField(patient, "id", 1L);

        Department cardiology = new Department("Cardiology", "Block A");
        doctor = new Doctor("D001", "Dr. Anil Kumar", "Cardiology", "anil@hospital.com", "9876500001",
                new BigDecimal("800.00"), cardiology);
        ReflectionTestUtils.setField(doctor, "id", 2L);
    }

    private CreateAppointmentRequest request(LocalDate date, String start, String end) {
        return new CreateAppointmentRequest(1L, 2L, date, LocalTime.parse(start), LocalTime.parse(end), "Chest pain");
    }

    private Appointment appointmentWithStatus(AppointmentStatus status) {
        Appointment appointment = new Appointment("APT-TEST0001", patient, doctor, nextWeek,
                LocalTime.of(10, 0), LocalTime.of(10, 30), "Chest pain", new BigDecimal("800.00"));
        appointment.setStatus(status);
        ReflectionTestUtils.setField(appointment, "id", 10L);
        ReflectionTestUtils.setField(appointment, "version", 0L);
        return appointment;
    }

    // ---------------- booking ----------------

    @Test
    void bookValidAppointment_isCreatedAsBooked_withTheDoctorsFee() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.countOverlappingAppointments(eq(2L), eq(nextWeek), any(), any(), any()))
                .thenReturn(0L);
        when(appointmentNumberGenerator.nextNumber()).thenReturn("APT-TEST0001");
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppointmentResponse response = appointmentService.bookAppointment(request(nextWeek, "10:00", "10:30"));

        assertThat(response.status()).isEqualTo(AppointmentStatus.BOOKED);
        assertThat(response.fee()).isEqualByComparingTo("800.00");          // copied from the doctor
        assertThat(response.appointmentNumber()).isEqualTo("APT-TEST0001");
        assertThat(response.patientNumber()).isEqualTo("P1001");
        assertThat(response.doctorCode()).isEqualTo("D001");
    }

    @Test
    void bookOverlappingAppointment_isRejected() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.countOverlappingAppointments(eq(2L), eq(nextWeek), any(), any(), any()))
                .thenReturn(1L);

        assertThatThrownBy(() -> appointmentService.bookAppointment(request(nextWeek, "10:15", "10:45")))
                .isInstanceOf(DoctorUnavailableException.class)
                .hasMessageContaining("overlapping 10:15-10:45");
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void bookInactiveDoctor_isRejected() {
        doctor.setActive(false);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(doctor));

        assertThatThrownBy(() -> appointmentService.bookAppointment(request(nextWeek, "10:00", "10:30")))
                .isInstanceOf(DoctorUnavailableException.class)
                .hasMessage("Doctor D001 is not active");
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void bookForInactivePatient_isRejected() {
        patient.setActive(false);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        assertThatThrownBy(() -> appointmentService.bookAppointment(request(nextWeek, "10:00", "10:30")))
                .isInstanceOf(PatientInactiveException.class);
    }

    @Test
    void bookPastAppointmentDate_isRejected() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(doctor));

        LocalDate yesterday = LocalDate.now().minusDays(1);
        assertThatThrownBy(() -> appointmentService.bookAppointment(request(yesterday, "10:00", "10:30")))
                .isInstanceOf(InvalidAppointmentTimeException.class)
                .hasMessageContaining("is in the past");
    }

    @Test
    void bookWithEndTimeBeforeStartTime_isRejected() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(doctorRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(doctor));

        assertThatThrownBy(() -> appointmentService.bookAppointment(request(nextWeek, "10:30", "10:00")))
                .isInstanceOf(InvalidAppointmentTimeException.class)
                .hasMessage("endTime must be after startTime");
    }

    // ---------------- lifecycle ----------------

    @Test
    void bookedAppointment_canBeConfirmed() {
        Appointment appointment = appointmentWithStatus(AppointmentStatus.BOOKED);
        when(appointmentRepository.findDetailsById(10L)).thenReturn(Optional.of(appointment));

        AppointmentResponse response = appointmentService.changeStatus(10L,
                new UpdateStatusRequest(AppointmentStatus.CONFIRMED, 0L));

        assertThat(response.status()).isEqualTo(AppointmentStatus.CONFIRMED);
        verify(appointmentRepository).saveAndFlush(appointment);
    }

    @Test
    void completedAppointment_cannotBeCancelled() {
        Appointment appointment = appointmentWithStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findDetailsById(10L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.changeStatus(10L,
                new UpdateStatusRequest(AppointmentStatus.CANCELLED, 0L)))
                .isInstanceOf(InvalidAppointmentStatusException.class)
                .hasMessage("Appointment APT-TEST0001 cannot change from COMPLETED to CANCELLED");
    }

    @Test
    void changeWithAnOldVersion_isRejected() {
        Appointment appointment = appointmentWithStatus(AppointmentStatus.BOOKED);
        ReflectionTestUtils.setField(appointment, "version", 3L);       // someone else changed it 3 times
        when(appointmentRepository.findDetailsById(10L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.changeStatus(10L,
                new UpdateStatusRequest(AppointmentStatus.CONFIRMED, 0L)))
                .isInstanceOf(AppointmentModifiedException.class);
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.BOOKED);
    }

    @Test
    void completeAppointment_changesStatus_andCreatesTheMedicalRecord() {
        Appointment appointment = appointmentWithStatus(AppointmentStatus.CONFIRMED);
        MedicalRecord record = new MedicalRecord(appointment, "Mild angina", "Aspirin 75mg", null, null);
        when(appointmentRepository.findDetailsById(10L)).thenReturn(Optional.of(appointment));
        when(medicalRecordService.createRecord(appointment, "Mild angina", "Aspirin 75mg", null, null))
                .thenReturn(record);

        var result = appointmentService.completeAppointment(10L,
                new CompleteAppointmentRequest(0L, "Mild angina", "Aspirin 75mg", null, null));

        assertThat(result.appointment().status()).isEqualTo(AppointmentStatus.COMPLETED);
        assertThat(result.medicalRecord().diagnosis()).isEqualTo("Mild angina");
    }

    @Test
    void searchSortedByAnUnknownField_isRejected() {
        assertThatThrownBy(() -> appointmentService.searchAppointments(null, null, null, null, null,
                PageRequest.of(0, 10, Sort.by("colour"))))
                .isInstanceOf(InvalidSearchCriteriaException.class)
                .hasMessageStartingWith("Cannot sort by 'colour'");
    }

    @Test
    void completeAppointment_thatIsOnlyBooked_isRejected() {
        Appointment appointment = appointmentWithStatus(AppointmentStatus.BOOKED);
        when(appointmentRepository.findDetailsById(10L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.completeAppointment(10L,
                new CompleteAppointmentRequest(0L, "Mild angina", null, null, null)))
                .isInstanceOf(InvalidAppointmentStatusException.class);
        verify(medicalRecordService, never()).createRecord(any(), any(), any(), any(), any());
    }
}
