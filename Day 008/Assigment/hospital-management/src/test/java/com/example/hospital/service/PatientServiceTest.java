package com.example.hospital.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.hospital.dto.PatientRequest;
import com.example.hospital.dto.PatientResponse;
import com.example.hospital.entity.Gender;
import com.example.hospital.entity.Patient;
import com.example.hospital.exception.DuplicatePatientException;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.PatientRepository;

/** Unit tests: PatientService with mocked repositories (no database). */
@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private PatientService patientService;

    private final PatientRequest request = new PatientRequest("p2001", "Kiran Das", LocalDate.of(1990, 5, 20),
            Gender.MALE, "kiran@example.com", "9000000009", "Pune");

    @Test
    void createPatient_isPersisted() {
        when(patientRepository.existsByPatientNumberIgnoreCase("P2001")).thenReturn(false);
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PatientResponse response = patientService.registerPatient(request);

        verify(patientRepository).save(any(Patient.class));
        assertThat(response.patientNumber()).isEqualTo("P2001");     // stored in upper case
        assertThat(response.name()).isEqualTo("Kiran Das");
        assertThat(response.active()).isTrue();
    }

    @Test
    void duplicatePatientNumber_isRejected() {
        when(patientRepository.existsByPatientNumberIgnoreCase("P2001")).thenReturn(true);

        assertThatThrownBy(() -> patientService.registerPatient(request))
                .isInstanceOf(DuplicatePatientException.class)
                .hasMessage("A patient with number P2001 already exists");
        verify(patientRepository, never()).save(any());
    }
}
