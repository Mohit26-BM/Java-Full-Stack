package com.example.hospital.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.hospital.dto.AppointmentCompletionResponse;
import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.CompleteAppointmentRequest;
import com.example.hospital.dto.CreateAppointmentRequest;
import com.example.hospital.dto.PageResponse;
import com.example.hospital.dto.UpdateStatusRequest;
import com.example.hospital.entity.Appointment;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.entity.Doctor;
import com.example.hospital.entity.MedicalRecord;
import com.example.hospital.entity.Patient;
import com.example.hospital.exception.AppointmentModifiedException;
import com.example.hospital.exception.AppointmentNotFoundException;
import com.example.hospital.exception.DoctorNotFoundException;
import com.example.hospital.exception.DoctorUnavailableException;
import com.example.hospital.exception.InvalidAppointmentStatusException;
import com.example.hospital.exception.InvalidAppointmentTimeException;
import com.example.hospital.exception.InvalidSearchCriteriaException;
import com.example.hospital.exception.PatientInactiveException;
import com.example.hospital.exception.PatientNotFoundException;
import com.example.hospital.mapper.AppointmentMapper;
import com.example.hospital.mapper.MedicalRecordMapper;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.PatientRepository;

/** Modules D and E - booking and the appointment lifecycle. */
@Service
@Transactional(readOnly = true)
public class AppointmentService {

    /** Appointments in these statuses occupy the doctor's time slot. CANCELLED and NO_SHOW free it. */
    public static final List<AppointmentStatus> BLOCKING_STATUSES =
            List.of(AppointmentStatus.BOOKED, AppointmentStatus.CONFIRMED, AppointmentStatus.COMPLETED);

    /** Fields a client may sort the appointment list by. */
    private static final List<String> SORTABLE_FIELDS =
            List.of("appointmentDate", "startTime", "status", "fee", "appointmentNumber", "createdAt");

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalRecordService medicalRecordService;
    private final AppointmentNumberGenerator appointmentNumberGenerator;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              MedicalRecordService medicalRecordService,
                              AppointmentNumberGenerator appointmentNumberGenerator) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.medicalRecordService = medicalRecordService;
        this.appointmentNumberGenerator = appointmentNumberGenerator;
    }

    // =====================================================================
    // Booking (section 12) - ONE transaction for the whole workflow
    // =====================================================================

    /**
     * The transaction is placed HERE (service method), not in the controller or
     * the repository: the checks (patient, doctor, overlap) and the INSERT must
     * succeed or fail together.
     *
     * Concurrency: the doctor row is locked (step 2), so two bookings for the same
     * doctor run one after the other. READ_COMMITTED makes the overlap check
     * (step 4) of the second booking see the appointment the first one has just
     * committed. (With MySQL's default REPEATABLE READ it would read an older
     * snapshot and could miss it.)
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AppointmentResponse bookAppointment(CreateAppointmentRequest request) {
        // 1. Load the patient; reject if not found or inactive
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new PatientNotFoundException(request.patientId()));
        if (!patient.getActive()) {
            throw new PatientInactiveException(patient.getPatientNumber());
        }

        // 2. Load the doctor (row locked until commit, see DoctorRepository); reject if not found or inactive
        Doctor doctor = doctorRepository.findByIdForUpdate(request.doctorId())
                .orElseThrow(() -> new DoctorNotFoundException(request.doctorId()));
        if (!doctor.getActive()) {
            throw new DoctorUnavailableException("Doctor " + doctor.getDoctorCode() + " is not active");
        }

        // 3. Validate the date and time (also checked by Bean Validation on the request,
        //    repeated here so the rule holds for every caller of this service)
        if (request.appointmentDate().isBefore(LocalDate.now())) {
            throw new InvalidAppointmentTimeException(
                    "appointmentDate " + request.appointmentDate() + " is in the past");
        }
        if (!request.endTime().isAfter(request.startTime())) {
            throw new InvalidAppointmentTimeException("endTime must be after startTime");
        }

        // 4. Check for overlapping appointments of this doctor (half-open slots [start, end))
        long overlapping = appointmentRepository.countOverlappingAppointments(doctor.getId(),
                request.appointmentDate(), request.startTime(), request.endTime(), BLOCKING_STATUSES);
        if (overlapping > 0) {
            throw new DoctorUnavailableException("Doctor " + doctor.getDoctorCode() + " already has an appointment"
                    + " overlapping " + request.startTime() + "-" + request.endTime() + " on " + request.appointmentDate());
        }

        // 5-7. Copy the doctor's CURRENT fee, generate the number, create the entity (status BOOKED)
        Appointment appointment = new Appointment(appointmentNumberGenerator.nextNumber(), patient, doctor,
                request.appointmentDate(), request.startTime(), request.endTime(), request.reason(),
                doctor.getConsultationFee());

        // 8. Persist  9. Return a DTO
        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentMapper.toResponse(saved);
    }

    // =====================================================================
    // Reading
    // =====================================================================

    public AppointmentResponse findById(Long id) {
        return AppointmentMapper.toResponse(getAppointmentWithDetails(id));
    }

    /** Query 6 - search with optional filters, pagination and sorting. */
    public PageResponse<AppointmentResponse> searchAppointments(Long doctorId, Long patientId, Long departmentId,
                                                                AppointmentStatus status, LocalDate date,
                                                                Pageable pageable) {
        // Only known appointment fields may be used in ?sort=... (anything else would
        // make Hibernate fail while building the ORDER BY)
        for (Sort.Order order : pageable.getSort()) {
            if (!SORTABLE_FIELDS.contains(order.getProperty())) {
                throw new InvalidSearchCriteriaException("Cannot sort by '" + order.getProperty()
                        + "'. Allowed: " + SORTABLE_FIELDS);
            }
        }
        return AppointmentMapper.toPageResponse(
                appointmentRepository.search(doctorId, patientId, departmentId, status, date, pageable));
    }

    /** Query 1 - the doctor's appointments on one date, ordered by start time. */
    public List<AppointmentResponse> getDoctorSchedule(Long doctorId, LocalDate date) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException(doctorId);
        }
        return appointmentRepository.findDoctorSchedule(doctorId, date).stream()
                .map(AppointmentMapper::toResponse)
                .toList();
    }

    // =====================================================================
    // Lifecycle (Module E)
    // =====================================================================

    /**
     * Changes the status if the transition is allowed and the client's version
     * is still the current one (optimistic locking).
     */
    @Transactional
    public AppointmentResponse changeStatus(Long id, UpdateStatusRequest request) {
        Appointment appointment = getAppointmentWithDetails(id);
        checkVersion(appointment, request.version());

        AppointmentStatus current = appointment.getStatus();
        AppointmentStatus next = request.status();
        if (!isAllowedTransition(current, next)) {
            throw new InvalidAppointmentStatusException(appointment.getAppointmentNumber(), current, next);
        }
        appointment.setStatus(next);

        // Send the UPDATE now (instead of at commit) so the response already shows
        // the new version number and updatedAt. Hibernate's UPDATE contains
        // "WHERE appointment_id = ? AND version = ?" because of @Version.
        appointmentRepository.saveAndFlush(appointment);
        return AppointmentMapper.toResponse(appointment);
    }

    /**
     * Section 17 - complete the appointment AND create its medical record as ONE
     * business operation. If creating the record fails, the status change is
     * rolled back too: the appointment stays CONFIRMED.
     */
    @Transactional
    public AppointmentCompletionResponse completeAppointment(Long id, CompleteAppointmentRequest request) {
        Appointment appointment = getAppointmentWithDetails(id);
        checkVersion(appointment, request.version());
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new InvalidAppointmentStatusException(appointment.getAppointmentNumber(),
                    appointment.getStatus(), AppointmentStatus.COMPLETED);
        }

        // Step 1: status change. Flushed now, so the UPDATE really runs in MySQL
        // inside this transaction (a later rollback has to undo it).
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.saveAndFlush(appointment);

        // Step 2: the medical record. Any exception here rolls back step 1 as well.
        MedicalRecord record = medicalRecordService.createRecord(appointment, request.diagnosis(),
                request.prescription(), request.doctorNotes(), request.followUpDate());

        return new AppointmentCompletionResponse(AppointmentMapper.toResponse(appointment),
                MedicalRecordMapper.toResponse(record));
    }

    // =====================================================================
    // Rules
    // =====================================================================

    /**
     *   BOOKED    -> CONFIRMED, CANCELLED, NO_SHOW
     *   CONFIRMED -> COMPLETED, CANCELLED, NO_SHOW
     *   COMPLETED, CANCELLED, NO_SHOW -> nothing (final)
     */
    private boolean isAllowedTransition(AppointmentStatus from, AppointmentStatus to) {
        switch (from) {
            case BOOKED:
                return to == AppointmentStatus.CONFIRMED
                        || to == AppointmentStatus.CANCELLED
                        || to == AppointmentStatus.NO_SHOW;
            case CONFIRMED:
                return to == AppointmentStatus.COMPLETED
                        || to == AppointmentStatus.CANCELLED
                        || to == AppointmentStatus.NO_SHOW;
            default:
                return false;
        }
    }

    /** The client must send the version it loaded; a different current version means someone else changed it. */
    private void checkVersion(Appointment appointment, Long clientVersion) {
        if (!appointment.getVersion().equals(clientVersion)) {
            throw new AppointmentModifiedException(appointment.getAppointmentNumber(), clientVersion,
                    appointment.getVersion());
        }
    }

    private Appointment getAppointmentWithDetails(Long id) {
        return appointmentRepository.findDetailsById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }
}
