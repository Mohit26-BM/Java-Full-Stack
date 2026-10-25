package com.example.hospital.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hospital.config.CacheConfig;
import com.example.hospital.dto.DoctorRequest;
import com.example.hospital.dto.DoctorResponse;
import com.example.hospital.dto.DoctorSummary;
import com.example.hospital.dto.DoctorUpdateRequest;
import com.example.hospital.entity.Department;
import com.example.hospital.entity.Doctor;
import com.example.hospital.exception.DepartmentInactiveException;
import com.example.hospital.exception.DepartmentNotFoundException;
import com.example.hospital.exception.DoctorNotFoundException;
import com.example.hospital.exception.DuplicateDoctorException;
import com.example.hospital.exception.InvalidAppointmentTimeException;
import com.example.hospital.exception.InvalidSearchCriteriaException;
import com.example.hospital.mapper.DoctorMapper;
import com.example.hospital.repository.DepartmentRepository;
import com.example.hospital.repository.DoctorRepository;

/** Module B - Doctor management. */
@Service
@Transactional(readOnly = true)
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;

    public DoctorService(DoctorRepository doctorRepository, DepartmentRepository departmentRepository) {
        this.doctorRepository = doctorRepository;
        this.departmentRepository = departmentRepository;
    }

    /** Registers a doctor in an existing, active department. */
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.DOCTOR_SUMMARIES, allEntries = true)
    public DoctorResponse registerDoctor(DoctorRequest request) {
        String doctorCode = request.doctorCode().trim().toUpperCase();
        String email = request.email().trim().toLowerCase();

        if (doctorRepository.existsByDoctorCodeIgnoreCase(doctorCode)) {
            throw new DuplicateDoctorException("A doctor with code " + doctorCode + " already exists");
        }
        if (doctorRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateDoctorException("A doctor with email " + email + " already exists");
        }
        Department department = getActiveDepartment(request.departmentId());

        Doctor doctor = new Doctor(doctorCode, request.name().trim(), request.specialization().trim(), email,
                request.phone(), request.consultationFee(), department);
        Doctor saved = doctorRepository.save(doctor);
        return DoctorMapper.toResponse(saved);
    }

    /** Updates the details, the consultation fee and (optionally) the department. */
    @Transactional
    @CacheEvict(cacheNames = CacheConfig.DOCTOR_SUMMARIES, allEntries = true)
    public DoctorResponse updateDoctor(Long id, DoctorUpdateRequest request) {
        Doctor doctor = getDoctor(id);
        String email = request.email().trim().toLowerCase();
        if (doctorRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateDoctorException("A doctor with email " + email + " already exists");
        }
        if (!doctor.getDepartment().getId().equals(request.departmentId())) {
            doctor.setDepartment(getActiveDepartment(request.departmentId()));
        }
        doctor.setName(request.name().trim());
        doctor.setSpecialization(request.specialization().trim());
        doctor.setEmail(email);
        doctor.setPhone(request.phone());
        doctor.setConsultationFee(request.consultationFee());
        return DoctorMapper.toResponse(doctor);
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.DOCTOR_SUMMARIES, allEntries = true)
    public DoctorResponse activateDoctor(Long id) {
        Doctor doctor = getDoctor(id);
        doctor.setActive(true);
        return DoctorMapper.toResponse(doctor);
    }

    @Transactional
    @CacheEvict(cacheNames = CacheConfig.DOCTOR_SUMMARIES, allEntries = true)
    public DoctorResponse deactivateDoctor(Long id) {
        Doctor doctor = getDoctor(id);
        doctor.setActive(false);
        return DoctorMapper.toResponse(doctor);
    }

    public DoctorResponse findById(Long id) {
        return DoctorMapper.toResponse(getDoctor(id));
    }

    /**
     * Search with ONE filter at a time:
     *   specialization            -> doctors of that specialization
     *   departmentId              -> active doctors of that department
     *   minFee + maxFee           -> doctors whose fee is in the range
     *   (no filter)               -> all active doctors
     */
    public List<DoctorResponse> searchDoctors(String specialization, Long departmentId,
                                              BigDecimal minFee, BigDecimal maxFee) {
        int filters = 0;
        if (specialization != null) {
            filters++;
        }
        if (departmentId != null) {
            filters++;
        }
        if (minFee != null || maxFee != null) {
            filters++;
        }
        if (filters > 1) {
            throw new InvalidSearchCriteriaException(
                    "Use only one filter: specialization, departmentId, or minFee + maxFee");
        }

        List<Doctor> doctors;
        if (specialization != null) {
            doctors = doctorRepository.findBySpecializationIgnoreCase(specialization.trim());
        } else if (departmentId != null) {
            if (!departmentRepository.existsById(departmentId)) {
                throw new DepartmentNotFoundException(departmentId);
            }
            doctors = doctorRepository.findByDepartmentIdAndActiveTrue(departmentId);
        } else if (minFee != null || maxFee != null) {
            if (minFee == null || maxFee == null) {
                throw new InvalidSearchCriteriaException("Both minFee and maxFee are required for a fee search");
            }
            if (minFee.compareTo(maxFee) > 0) {
                throw new InvalidSearchCriteriaException("minFee must not be greater than maxFee");
            }
            doctors = doctorRepository.findByConsultationFeeBetween(minFee, maxFee);
        } else {
            doctors = doctorRepository.findByActiveTrueOrderByNameAsc();
        }
        return doctors.stream().map(DoctorMapper::toResponse).toList();
    }

    /** Lightweight list of active doctors (JPQL DTO projection). */
    @Cacheable(CacheConfig.DOCTOR_SUMMARIES)
    public List<DoctorSummary> findActiveDoctorSummaries() {
        return doctorRepository.findActiveDoctorSummaries();
    }

    /** Query 5 - active doctors of a specialization who are free for the whole slot. */
    public List<DoctorSummary> findAvailableDoctors(String specialization, LocalDate date,
                                                    LocalTime startTime, LocalTime endTime) {
        if (!endTime.isAfter(startTime)) {
            throw new InvalidAppointmentTimeException("endTime must be after startTime");
        }
        return doctorRepository.findAvailableDoctors(specialization.trim(), date, startTime, endTime,
                AppointmentService.BLOCKING_STATUSES);
    }

    private Doctor getDoctor(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }

    private Department getActiveDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));
        if (!department.getActive()) {
            throw new DepartmentInactiveException(department.getName());
        }
        return department;
    }
}
