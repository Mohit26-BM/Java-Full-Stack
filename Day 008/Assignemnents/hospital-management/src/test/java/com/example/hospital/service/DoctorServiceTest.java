package com.example.hospital.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.hospital.dto.DoctorRequest;
import com.example.hospital.dto.DoctorResponse;
import com.example.hospital.entity.Department;
import com.example.hospital.entity.Doctor;
import com.example.hospital.exception.DepartmentInactiveException;
import com.example.hospital.exception.DuplicateDoctorException;
import com.example.hospital.repository.DepartmentRepository;
import com.example.hospital.repository.DoctorRepository;

/** Unit tests: DoctorService with mocked repositories. */
@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DoctorService doctorService;

    private final DoctorRequest request = new DoctorRequest("d004", "Dr. Kavya Nair", "Pediatrics",
            "Kavya.Nair@hospital.com", "9876500004", new BigDecimal("600.00"), 4L);

    @Test
    void createDoctorWithDepartment_linksTheDepartment() {
        Department pediatrics = new Department("Pediatrics", "Block C");
        when(doctorRepository.existsByDoctorCodeIgnoreCase("D004")).thenReturn(false);
        when(doctorRepository.existsByEmailIgnoreCase("kavya.nair@hospital.com")).thenReturn(false);
        when(departmentRepository.findById(4L)).thenReturn(Optional.of(pediatrics));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DoctorResponse response = doctorService.registerDoctor(request);

        ArgumentCaptor<Doctor> saved = ArgumentCaptor.forClass(Doctor.class);
        verify(doctorRepository).save(saved.capture());
        assertThat(saved.getValue().getDepartment()).isSameAs(pediatrics);   // the relationship is set
        assertThat(response.departmentName()).isEqualTo("Pediatrics");
        assertThat(response.doctorCode()).isEqualTo("D004");
        assertThat(response.email()).isEqualTo("kavya.nair@hospital.com");
    }

    @Test
    void createDoctor_inInactiveDepartment_isRejected() {
        Department closed = new Department("Pediatrics", "Block C");
        closed.setActive(false);
        when(departmentRepository.findById(4L)).thenReturn(Optional.of(closed));

        assertThatThrownBy(() -> doctorService.registerDoctor(request))
                .isInstanceOf(DepartmentInactiveException.class);
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void createDoctor_withDuplicateCode_isRejected() {
        when(doctorRepository.existsByDoctorCodeIgnoreCase("D004")).thenReturn(true);

        assertThatThrownBy(() -> doctorService.registerDoctor(request))
                .isInstanceOf(DuplicateDoctorException.class)
                .hasMessage("A doctor with code D004 already exists");
    }
}
