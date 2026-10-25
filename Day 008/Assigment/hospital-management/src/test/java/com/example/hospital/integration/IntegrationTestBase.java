package com.example.hospital.integration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;

import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.CreateAppointmentRequest;
import com.example.hospital.dto.DepartmentRequest;
import com.example.hospital.dto.DepartmentResponse;
import com.example.hospital.dto.DoctorRequest;
import com.example.hospital.dto.DoctorResponse;
import com.example.hospital.dto.PatientRequest;
import com.example.hospital.dto.PatientResponse;
import com.example.hospital.entity.Gender;
import com.example.hospital.event.NotificationService;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.AppointmentStatusChangeRepository;
import com.example.hospital.repository.DoctorLeaveRepository;
import com.example.hospital.repository.DoctorWorkingHoursRepository;
import com.example.hospital.repository.DepartmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.MedicalRecordRepository;
import com.example.hospital.repository.PatientRepository;
import com.example.hospital.service.AppointmentService;
import com.example.hospital.service.DepartmentService;
import com.example.hospital.service.DoctorScheduleService;
import com.example.hospital.service.DoctorService;
import com.example.hospital.service.MedicalRecordService;
import com.example.hospital.service.PatientService;

/**
 * Common setup for the integration tests.
 *
 * @SpringBootTest starts the whole application against the real MySQL test
 * database (hospital_management_test, see src/test/resources/application.properties).
 * There is NO @Transactional on the tests: every service call commits for real,
 * exactly as in production. Therefore all tables are emptied before each test.
 */
@SpringBootTest
abstract class IntegrationTestBase {

    @Autowired
    protected DepartmentService departmentService;
    @Autowired
    protected DoctorService doctorService;
    @Autowired
    protected PatientService patientService;
    @Autowired
    protected AppointmentService appointmentService;
    @Autowired
    protected MedicalRecordService medicalRecordService;

    @Autowired
    protected DepartmentRepository departmentRepository;
    @Autowired
    protected DoctorRepository doctorRepository;
    @Autowired
    protected PatientRepository patientRepository;
    @Autowired
    protected AppointmentRepository appointmentRepository;
    @Autowired
    protected MedicalRecordRepository medicalRecordRepository;

    // Day 09 extensions
    @Autowired
    protected DoctorScheduleService doctorScheduleService;
    @Autowired
    protected AppointmentStatusChangeRepository statusChangeRepository;
    @Autowired
    protected DoctorWorkingHoursRepository workingHoursRepository;
    @Autowired
    protected DoctorLeaveRepository leaveRepository;
    @Autowired
    protected NotificationService notificationService;
    @Autowired
    protected CacheManager cacheManager;

    /** A date safely in the future, so booking is allowed. */
    protected final LocalDate visitDate = LocalDate.now().plusDays(10);

    @BeforeEach
    void cleanDatabase() {
        // children first, because of the foreign keys
        statusChangeRepository.deleteAllInBatch();
        medicalRecordRepository.deleteAllInBatch();
        appointmentRepository.deleteAllInBatch();
        workingHoursRepository.deleteAllInBatch();
        leaveRepository.deleteAllInBatch();
        doctorRepository.deleteAllInBatch();
        patientRepository.deleteAllInBatch();
        departmentRepository.deleteAllInBatch();

        // The tables were emptied behind the services' backs, so cached lists and
        // the in-memory notifications from earlier tests are cleared as well.
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
        notificationService.clear();
    }

    protected DepartmentResponse createDepartment(String name) {
        return departmentService.createDepartment(new DepartmentRequest(name, "Block A"));
    }

    protected DoctorResponse createDoctor(String code, Long departmentId, String fee) {
        return doctorService.registerDoctor(new DoctorRequest(code, "Dr. " + code, "Cardiology",
                code.toLowerCase() + "@hospital.com", "9876500001", new BigDecimal(fee), departmentId));
    }

    protected PatientResponse createPatient(String patientNumber) {
        return patientService.registerPatient(new PatientRequest(patientNumber, "Patient " + patientNumber,
                LocalDate.of(1990, 1, 1), Gender.FEMALE, null, "9000000001", null));
    }

    protected AppointmentResponse book(Long patientId, Long doctorId, LocalDate date, String start, String end) {
        return appointmentService.bookAppointment(new CreateAppointmentRequest(patientId, doctorId, date,
                LocalTime.parse(start), LocalTime.parse(end), "Checkup"));
    }
}
