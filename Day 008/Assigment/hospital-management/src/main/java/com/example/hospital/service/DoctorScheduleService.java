package com.example.hospital.service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.example.hospital.dto.DoctorSlotsResponse;
import com.example.hospital.dto.LeaveRequest;
import com.example.hospital.dto.LeaveResponse;
import com.example.hospital.dto.TimeSlot;
import com.example.hospital.dto.WeeklyScheduleRequest;
import com.example.hospital.dto.WorkingHoursRequest;
import com.example.hospital.dto.WorkingHoursResponse;
import com.example.hospital.entity.Appointment;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.entity.Doctor;
import com.example.hospital.entity.DoctorLeave;
import com.example.hospital.entity.DoctorWorkingHours;
import com.example.hospital.exception.DoctorNotFoundException;
import com.example.hospital.exception.DoctorUnavailableException;
import com.example.hospital.exception.InvalidScheduleException;
import com.example.hospital.exception.LeaveConflictException;
import com.example.hospital.exception.LeaveNotFoundException;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorLeaveRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.DoctorWorkingHoursRepository;

/**
 * Extension: doctor working hours, leave, and automatic slot generation.
 *
 * Rules
 *  - A doctor WITHOUT a timetable can be booked at any time (the behaviour of the
 *    core assignment). Once a timetable exists, a booking must fit inside that
 *    weekday's hours, and weekdays that are not listed are days off.
 *  - A doctor on leave cannot be booked, with or without a timetable.
 *  - A leave cannot overlap another leave, and cannot be added while the doctor
 *    still has BOOKED or CONFIRMED appointments in that period.
 */
@Service
@Transactional(readOnly = true)
public class DoctorScheduleService {

    /** Appointments that a leave would clash with (a future appointment cannot be COMPLETED yet). */
    private static final List<AppointmentStatus> OPEN_STATUSES =
            List.of(AppointmentStatus.BOOKED, AppointmentStatus.CONFIRMED);

    private final DoctorRepository doctorRepository;
    private final DoctorWorkingHoursRepository workingHoursRepository;
    private final DoctorLeaveRepository leaveRepository;
    private final AppointmentRepository appointmentRepository;
    private final Clock clock;

    public DoctorScheduleService(DoctorRepository doctorRepository,
                                 DoctorWorkingHoursRepository workingHoursRepository,
                                 DoctorLeaveRepository leaveRepository,
                                 AppointmentRepository appointmentRepository,
                                 Clock clock) {
        this.doctorRepository = doctorRepository;
        this.workingHoursRepository = workingHoursRepository;
        this.leaveRepository = leaveRepository;
        this.appointmentRepository = appointmentRepository;
        this.clock = clock;
    }

    // =====================================================================
    // Working hours
    // =====================================================================

    /** Replaces the whole weekly timetable in one transaction: delete the old rows, insert the new ones. */
    @Transactional
    public List<WorkingHoursResponse> replaceWorkingHours(Long doctorId, WeeklyScheduleRequest request) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException(doctorId);
        }
        Set<DayOfWeek> seen = EnumSet.noneOf(DayOfWeek.class);
        for (WorkingHoursRequest day : request.days()) {
            if (!seen.add(day.dayOfWeek())) {
                throw new InvalidScheduleException(day.dayOfWeek() + " is listed more than once");
            }
        }

        // One DELETE statement. It runs before the INSERTs, so the UNIQUE (doctor, day) constraint is not hit.
        workingHoursRepository.deleteAllForDoctor(doctorId);

        // A reference (proxy) is enough to set the foreign key: no SELECT of the doctor is needed.
        Doctor doctor = doctorRepository.getReferenceById(doctorId);
        List<DoctorWorkingHours> rows = request.days().stream()
                .map(day -> new DoctorWorkingHours(doctor, day.dayOfWeek(), day.startTime(), day.endTime(),
                        day.slotMinutes()))
                .toList();
        workingHoursRepository.saveAll(rows);
        return toSortedResponses(rows);
    }

    public List<WorkingHoursResponse> getWorkingHours(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException(doctorId);
        }
        return toSortedResponses(workingHoursRepository.findByDoctorId(doctorId));
    }

    // =====================================================================
    // Leave
    // =====================================================================

    /**
     * Adds a leave period. The doctor row is locked (SELECT ... FOR UPDATE), the
     * same lock that booking takes, so a booking and a leave for the same doctor
     * cannot slip past each other's checks. READ_COMMITTED lets the appointment
     * count see a booking that committed while this transaction was waiting.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public LeaveResponse addLeave(Long doctorId, LeaveRequest request) {
        Doctor doctor = doctorRepository.findByIdForUpdate(doctorId)
                .orElseThrow(() -> new DoctorNotFoundException(doctorId));

        if (leaveRepository.existsByDoctorIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                doctorId, request.endDate(), request.startDate())) {
            throw new LeaveConflictException("LEAVE_OVERLAP", "Doctor " + doctor.getDoctorCode()
                    + " already has leave overlapping " + request.startDate() + " to " + request.endDate());
        }
        long appointments = appointmentRepository.countBlockingAppointmentsBetween(doctorId,
                request.startDate(), request.endDate(), OPEN_STATUSES);
        if (appointments > 0) {
            throw new LeaveConflictException("LEAVE_HAS_APPOINTMENTS", "Doctor " + doctor.getDoctorCode()
                    + " has " + appointments + " booked or confirmed appointment(s) between " + request.startDate()
                    + " and " + request.endDate() + ". Cancel or move them before adding the leave.");
        }

        DoctorLeave leave = leaveRepository.save(new DoctorLeave(doctor, request.startDate(), request.endDate(),
                request.reason()));
        return toResponse(leave);
    }

    public List<LeaveResponse> getLeaves(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new DoctorNotFoundException(doctorId);
        }
        return leaveRepository.findByDoctorIdOrderByStartDate(doctorId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deleteLeave(Long doctorId, Long leaveId) {
        DoctorLeave leave = leaveRepository.findByIdAndDoctorId(leaveId, doctorId)
                .orElseThrow(() -> new LeaveNotFoundException(doctorId, leaveId));
        leaveRepository.delete(leave);
    }

    // =====================================================================
    // Used by the booking workflow (runs inside the booking transaction)
    // =====================================================================

    /**
     * Throws DoctorUnavailableException if the doctor is on leave that day, or has
     * a timetable and the slot is not inside that weekday's working hours.
     */
    public void checkDoctorIsWorking(Doctor doctor, LocalDate date, LocalTime start, LocalTime end) {
        Optional<DoctorLeave> leave = findLeaveOn(doctor.getId(), date);
        if (leave.isPresent()) {
            throw new DoctorUnavailableException("Doctor " + doctor.getDoctorCode() + " is on leave on " + date
                    + " (" + describe(leave.get()) + ")");
        }
        if (!workingHoursRepository.existsByDoctorId(doctor.getId())) {
            return;   // no timetable: any time is accepted
        }
        DayOfWeek day = date.getDayOfWeek();
        DoctorWorkingHours hours = workingHoursRepository.findByDoctorIdAndDayOfWeek(doctor.getId(), day)
                .orElseThrow(() -> new DoctorUnavailableException("Doctor " + doctor.getDoctorCode()
                        + " does not work on " + day));
        if (!hours.covers(start, end)) {
            throw new DoctorUnavailableException("Doctor " + doctor.getDoctorCode() + " works "
                    + hours.getStartTime() + "-" + hours.getEndTime() + " on " + day + "; " + start + "-" + end
                    + " is outside these hours");
        }
    }

    // =====================================================================
    // Slot generation
    // =====================================================================

    /**
     * Splits the doctor's working hours for that weekday into slots of
     * slotMinutes and marks each one FREE, BOOKED (overlaps a blocking
     * appointment, same half-open rule as booking) or PAST.
     */
    public DoctorSlotsResponse getSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new DoctorNotFoundException(doctorId));
        DayOfWeek day = date.getDayOfWeek();

        if (!doctor.getActive()) {
            return noSlots(doctor, date, "INACTIVE", "Doctor " + doctor.getDoctorCode() + " is not active");
        }
        Optional<DoctorLeave> leave = findLeaveOn(doctorId, date);
        if (leave.isPresent()) {
            return noSlots(doctor, date, "ON_LEAVE", "On leave: " + describe(leave.get()));
        }
        if (!workingHoursRepository.existsByDoctorId(doctorId)) {
            return noSlots(doctor, date, "NO_TIMETABLE",
                    "No working hours are set, so slots cannot be generated. Bookings are accepted at any time.");
        }
        Optional<DoctorWorkingHours> found = workingHoursRepository.findByDoctorIdAndDayOfWeek(doctorId, day);
        if (found.isEmpty()) {
            return noSlots(doctor, date, "DAY_OFF", "Doctor " + doctor.getDoctorCode() + " does not work on " + day);
        }
        DoctorWorkingHours hours = found.get();

        List<Appointment> booked = appointmentRepository.findBlockingAppointments(doctorId, date,
                AppointmentService.BLOCKING_STATUSES);
        LocalDateTime now = LocalDateTime.now(clock);

        // Minutes since midnight avoid LocalTime wrapping round past 23:59
        int first = hours.getStartTime().toSecondOfDay() / 60;
        int last = hours.getEndTime().toSecondOfDay() / 60;
        int length = hours.getSlotMinutes();
        List<TimeSlot> slots = new ArrayList<>();
        for (int minute = first; minute + length <= last; minute += length) {
            LocalTime start = LocalTime.ofSecondOfDay(minute * 60L);
            LocalTime end = LocalTime.ofSecondOfDay((minute + length) * 60L);
            String status;
            if (!LocalDateTime.of(date, start).isAfter(now)) {
                status = "PAST";
            } else if (booked.stream().anyMatch(a -> a.getStartTime().isBefore(end) && a.getEndTime().isAfter(start))) {
                status = "BOOKED";
            } else {
                status = "FREE";
            }
            slots.add(new TimeSlot(start, end, status.equals("FREE"), status));
        }
        long free = slots.stream().filter(TimeSlot::available).count();
        return new DoctorSlotsResponse(doctor.getId(), doctor.getDoctorCode(), doctor.getName(), date, day,
                "WORKING", null, hours.getStartTime(), hours.getEndTime(), length, free, slots);
    }

    // =====================================================================
    // helpers
    // =====================================================================

    private Optional<DoctorLeave> findLeaveOn(Long doctorId, LocalDate date) {
        return leaveRepository.findFirstByDoctorIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(doctorId, date, date);
    }

    private DoctorSlotsResponse noSlots(Doctor doctor, LocalDate date, String dayStatus, String message) {
        return new DoctorSlotsResponse(doctor.getId(), doctor.getDoctorCode(), doctor.getName(), date,
                date.getDayOfWeek(), dayStatus, message, null, null, null, 0, List.of());
    }

    private static String describe(DoctorLeave leave) {
        String period = leave.getStartDate() + " to " + leave.getEndDate();
        return leave.getReason() == null ? period : leave.getReason() + ", " + period;
    }

    private List<WorkingHoursResponse> toSortedResponses(List<DoctorWorkingHours> rows) {
        return rows.stream()
                .sorted(Comparator.comparing(DoctorWorkingHours::getDayOfWeek))
                .map(h -> new WorkingHoursResponse(h.getDayOfWeek(), h.getStartTime(), h.getEndTime(), h.getSlotMinutes()))
                .toList();
    }

    private LeaveResponse toResponse(DoctorLeave leave) {
        return new LeaveResponse(leave.getId(), leave.getDoctor().getId(), leave.getStartDate(), leave.getEndDate(),
                leave.getReason());
    }
}
