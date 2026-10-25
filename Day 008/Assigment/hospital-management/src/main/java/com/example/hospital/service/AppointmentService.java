package com.example.hospital.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
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
import com.example.hospital.dto.StatusChangeResponse;
import com.example.hospital.dto.UpdateStatusRequest;
import com.example.hospital.entity.Appointment;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.entity.AppointmentStatusChange;
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
import com.example.hospital.event.AppointmentBookedEvent;
import com.example.hospital.event.AppointmentStatusChangedEvent;
import com.example.hospital.mapper.AppointmentMapper;
import com.example.hospital.mapper.MedicalRecordMapper;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.AppointmentStatusChangeRepository;
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

    /** Appointments in these statuses are still waiting to happen; the NO_SHOW job looks only at these. */
    public static final List<AppointmentStatus> OPEN_STATUSES =
            List.of(AppointmentStatus.BOOKED, AppointmentStatus.CONFIRMED);

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalRecordService medicalRecordService;
    private final AppointmentNumberGenerator appointmentNumberGenerator;
    private final DoctorScheduleService doctorScheduleService;
    private final AppointmentStatusChangeRepository statusChangeRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              MedicalRecordService medicalRecordService,
                              AppointmentNumberGenerator appointmentNumberGenerator,
                              DoctorScheduleService doctorScheduleService,
                              AppointmentStatusChangeRepository statusChangeRepository,
                              ApplicationEventPublisher eventPublisher,
                              Clock clock) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.medicalRecordService = medicalRecordService;
        this.appointmentNumberGenerator = appointmentNumberGenerator;
        this.doctorScheduleService = doctorScheduleService;
        this.statusChangeRepository = statusChangeRepository;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
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
        if (request.appointmentDate().isBefore(LocalDate.now(clock))) {
            throw new InvalidAppointmentTimeException(
                    "appointmentDate " + request.appointmentDate() + " is in the past");
        }
        if (!request.endTime().isAfter(request.startTime())) {
            throw new InvalidAppointmentTimeException("endTime must be after startTime");
        }

        // 3b. Extension: the doctor must not be on leave, and (if a timetable exists) the
        //     slot must be inside that weekday's working hours
        doctorScheduleService.checkDoctorIsWorking(doctor, request.appointmentDate(), request.startTime(),
                request.endTime());

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

        // 8. Persist
        Appointment saved = appointmentRepository.save(appointment);

        // Extensions: first row of the audit trail, and the "booked" event. The
        // notification listener only runs once this transaction has committed.
        recordStatusChange(saved, null, AppointmentStatus.BOOKED, AppointmentStatusChange.BY_API, "Booked");
        eventPublisher.publishEvent(new AppointmentBookedEvent(saved.getAppointmentNumber(), patient.getName(),
                patient.getEmail(), patient.getPhone(), doctor.getName(), saved.getAppointmentDate(),
                saved.getStartTime()));

        // 9. Return a DTO
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

        recordStatusChange(appointment, current, next, AppointmentStatusChange.BY_API, null);
        publishStatusChanged(appointment, current, AppointmentStatusChange.BY_API);
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
        // Extension: the audit row is part of the same transaction, so a rollback removes it too
        recordStatusChange(appointment, AppointmentStatus.CONFIRMED, AppointmentStatus.COMPLETED,
                AppointmentStatusChange.BY_API, "Completed with a medical record");

        // Step 2: the medical record. Any exception here rolls back step 1 as well.
        MedicalRecord record = medicalRecordService.createRecord(appointment, request.diagnosis(),
                request.prescription(), request.doctorNotes(), request.followUpDate());

        publishStatusChanged(appointment, AppointmentStatus.CONFIRMED, AppointmentStatusChange.BY_API);
        return new AppointmentCompletionResponse(AppointmentMapper.toResponse(appointment),
                MedicalRecordMapper.toResponse(record));
    }

    // =====================================================================
    // Extension: status history (audit table)
    // =====================================================================

    public List<StatusChangeResponse> getStatusHistory(Long appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new AppointmentNotFoundException(appointmentId);
        }
        return statusChangeRepository.findByAppointmentIdOrderByChangedAtAscIdAsc(appointmentId).stream()
                .map(c -> new StatusChangeResponse(c.getFromStatus(), c.getToStatus(), c.getChangedAt(),
                        c.getChangedBy(), c.getNote()))
                .toList();
    }

    // =====================================================================
    // Extension: missed appointments (used by MissedAppointmentJob)
    // =====================================================================

    /** Ids of BOOKED/CONFIRMED appointments that ended at or before the cut-off moment. */
    public List<Long> findOpenAppointmentIdsEndedBefore(LocalDateTime cutoff) {
        return appointmentRepository.findOpenAppointmentIdsEndedBefore(OPEN_STATUSES, cutoff.toLocalDate(),
                cutoff.toLocalTime());
    }

    /**
     * Marks ONE appointment as NO_SHOW in its own transaction. The status is
     * checked again here, because between the job's search and this update a
     * receptionist may have completed or cancelled it. Returns the appointment
     * number when it was marked, or empty when it was left alone.
     *
     * A concurrent update of the same row makes the @Version check fail
     * (ObjectOptimisticLockingFailureException); the job catches it and skips
     * this appointment instead of overwriting the other change.
     */
    @Transactional
    public Optional<String> markNoShowIfStillOpen(Long id, LocalDateTime cutoff, String note) {
        Appointment appointment = getAppointmentWithDetails(id);
        boolean stillOpen = OPEN_STATUSES.contains(appointment.getStatus());
        boolean ended = !LocalDateTime.of(appointment.getAppointmentDate(), appointment.getEndTime()).isAfter(cutoff);
        if (!stillOpen || !ended) {
            return Optional.empty();
        }
        AppointmentStatus previous = appointment.getStatus();
        appointment.setStatus(AppointmentStatus.NO_SHOW);
        appointmentRepository.saveAndFlush(appointment);
        recordStatusChange(appointment, previous, AppointmentStatus.NO_SHOW, AppointmentStatusChange.BY_NO_SHOW_JOB, note);
        publishStatusChanged(appointment, previous, AppointmentStatusChange.BY_NO_SHOW_JOB);
        return Optional.of(appointment.getAppointmentNumber());
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

    /** Extension: one audit row. Runs inside the caller's transaction. */
    private void recordStatusChange(Appointment appointment, AppointmentStatus from, AppointmentStatus to,
                                    String changedBy, String note) {
        statusChangeRepository.save(new AppointmentStatusChange(appointment, from, to, changedBy, note));
    }

    /** Extension: the patient and doctor are already loaded (JOIN FETCH), so reading their names is free. */
    private void publishStatusChanged(Appointment appointment, AppointmentStatus from, String changedBy) {
        Patient patient = appointment.getPatient();
        eventPublisher.publishEvent(new AppointmentStatusChangedEvent(appointment.getAppointmentNumber(), from,
                appointment.getStatus(), changedBy, patient.getName(), patient.getEmail(), patient.getPhone(),
                appointment.getDoctor().getName(), appointment.getAppointmentDate(), appointment.getStartTime()));
    }

    private Appointment getAppointmentWithDetails(Long id) {
        return appointmentRepository.findDetailsById(id)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }
}
