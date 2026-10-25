package com.example.hospital.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.DoctorSlotsResponse;
import com.example.hospital.dto.LeaveRequest;
import com.example.hospital.dto.TimeSlot;
import com.example.hospital.dto.UpdateStatusRequest;
import com.example.hospital.dto.WeeklyScheduleRequest;
import com.example.hospital.dto.WorkingHoursRequest;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.exception.DoctorUnavailableException;
import com.example.hospital.exception.InvalidScheduleException;
import com.example.hospital.exception.LeaveConflictException;

/** Extension: working hours, leave and slot generation against the real database. */
class DoctorScheduleIntegrationTest extends IntegrationTestBase {

    private Long doctorId;
    private Long patientId;

    /** A Monday at least a week ahead, so every date used here is in the future. */
    private final LocalDate monday = LocalDate.now().plusWeeks(1).with(TemporalAdjusters.next(DayOfWeek.MONDAY));

    @BeforeEach
    void createReferenceData() {
        Long departmentId = createDepartment("Cardiology").id();
        doctorId = createDoctor("D001", departmentId, "800.00").id();
        patientId = createPatient("P1001").id();
    }

    /** Monday to Friday 09:00-12:00 in 30-minute slots. */
    private void giveDoctorWeekdayMornings() {
        List<WorkingHoursRequest> days = List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY).stream()
                .map(day -> new WorkingHoursRequest(day, LocalTime.of(9, 0), LocalTime.of(12, 0), 30))
                .toList();
        doctorScheduleService.replaceWorkingHours(doctorId, new WeeklyScheduleRequest(days));
    }

    // ---------------- booking rules ----------------

    @Test
    void withoutTimetable_doctorCanBeBookedAtAnyTime() {
        AppointmentResponse evening = book(patientId, doctorId, monday.plusDays(6), "20:00", "20:30");   // a Sunday evening
        assertThat(evening.status()).isEqualTo(AppointmentStatus.BOOKED);
    }

    @Test
    void withTimetable_bookingInsideHoursIsAccepted_andTheLastSlotEndingAtCloseIsAllowed() {
        giveDoctorWeekdayMornings();

        assertThat(book(patientId, doctorId, monday, "09:00", "09:30").status()).isEqualTo(AppointmentStatus.BOOKED);
        // 11:30-12:00 ends exactly when the doctor stops working: allowed (half-open, like appointments)
        assertThat(book(patientId, doctorId, monday, "11:30", "12:00").status()).isEqualTo(AppointmentStatus.BOOKED);
    }

    @Test
    void withTimetable_bookingOutsideHoursIsRejected() {
        giveDoctorWeekdayMornings();

        assertThatThrownBy(() -> book(patientId, doctorId, monday, "11:45", "12:15"))
                .isInstanceOf(DoctorUnavailableException.class)
                .hasMessageContaining("works 09:00-12:00 on MONDAY");
        assertThatThrownBy(() -> book(patientId, doctorId, monday, "08:30", "09:00"))
                .isInstanceOf(DoctorUnavailableException.class);
    }

    @Test
    void withTimetable_bookingOnADayOffIsRejected() {
        giveDoctorWeekdayMornings();

        assertThatThrownBy(() -> book(patientId, doctorId, monday.plusDays(5), "10:00", "10:30"))   // Saturday
                .isInstanceOf(DoctorUnavailableException.class)
                .hasMessageContaining("does not work on SATURDAY");
    }

    @Test
    void bookingDuringLeaveIsRejected_evenWithoutTimetable() {
        doctorScheduleService.addLeave(doctorId, new LeaveRequest(monday, monday.plusDays(2), "Conference"));

        assertThatThrownBy(() -> book(patientId, doctorId, monday.plusDays(1), "10:00", "10:30"))
                .isInstanceOf(DoctorUnavailableException.class)
                .hasMessageContaining("is on leave")
                .hasMessageContaining("Conference");
        // the day after the leave ends is fine (end date is inclusive, so the next day is free)
        assertThat(book(patientId, doctorId, monday.plusDays(3), "10:00", "10:30").status())
                .isEqualTo(AppointmentStatus.BOOKED);
    }

    // ---------------- leave rules ----------------

    @Test
    void overlappingLeaveIsRejected() {
        doctorScheduleService.addLeave(doctorId, new LeaveRequest(monday, monday.plusDays(4), "Holiday"));

        assertThatThrownBy(() -> doctorScheduleService.addLeave(doctorId,
                new LeaveRequest(monday.plusDays(4), monday.plusDays(6), "Extra")))   // shares the last day
                .isInstanceOf(LeaveConflictException.class)
                .extracting("errorCode").isEqualTo("LEAVE_OVERLAP");
    }

    @Test
    void leaveOverABookedAppointmentIsRejected_butACancelledOneDoesNotBlockIt() {
        AppointmentResponse booked = book(patientId, doctorId, monday.plusDays(1), "10:00", "10:30");

        assertThatThrownBy(() -> doctorScheduleService.addLeave(doctorId,
                new LeaveRequest(monday, monday.plusDays(2), "Holiday")))
                .isInstanceOf(LeaveConflictException.class)
                .extracting("errorCode").isEqualTo("LEAVE_HAS_APPOINTMENTS");

        appointmentService.changeStatus(booked.id(), new UpdateStatusRequest(AppointmentStatus.CANCELLED, booked.version()));
        assertThat(doctorScheduleService.addLeave(doctorId, new LeaveRequest(monday, monday.plusDays(2), "Holiday")).id())
                .isNotNull();
    }

    @Test
    void timetableWithTheSameWeekdayTwiceIsRejected() {
        WeeklyScheduleRequest twice = new WeeklyScheduleRequest(List.of(
                new WorkingHoursRequest(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(12, 0), 30),
                new WorkingHoursRequest(DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(17, 0), 30)));

        assertThatThrownBy(() -> doctorScheduleService.replaceWorkingHours(doctorId, twice))
                .isInstanceOf(InvalidScheduleException.class)
                .hasMessageContaining("MONDAY is listed more than once");
    }

    @Test
    void replacingTheTimetableRemovesTheOldDays() {
        giveDoctorWeekdayMornings();
        doctorScheduleService.replaceWorkingHours(doctorId, new WeeklyScheduleRequest(List.of(
                new WorkingHoursRequest(DayOfWeek.SATURDAY, LocalTime.of(10, 0), LocalTime.of(14, 0), 60))));

        assertThat(doctorScheduleService.getWorkingHours(doctorId))
                .extracting("dayOfWeek").containsExactly(DayOfWeek.SATURDAY);
    }

    // ---------------- slot generation ----------------

    @Test
    void slotsAreGeneratedFromWorkingHours_andBookedSlotsAreMarked() {
        giveDoctorWeekdayMornings();
        book(patientId, doctorId, monday, "10:00", "10:30");

        DoctorSlotsResponse result = doctorScheduleService.getSlots(doctorId, monday);

        assertThat(result.dayStatus()).isEqualTo("WORKING");
        assertThat(result.slots()).hasSize(6);                     // 09:00 to 12:00 in 30-minute slots
        assertThat(result.slots()).extracting(TimeSlot::start).extracting(LocalTime::toString)
                .containsExactly("09:00", "09:30", "10:00", "10:30", "11:00", "11:30");
        assertThat(result.slots().get(2).status()).isEqualTo("BOOKED");   // 10:00-10:30
        assertThat(result.slots().get(3).status()).isEqualTo("FREE");     // 10:30-11:00: back-to-back, not blocked
        assertThat(result.freeSlots()).isEqualTo(5);
    }

    @Test
    void aCancelledAppointmentFreesItsSlot() {
        giveDoctorWeekdayMornings();
        AppointmentResponse booked = book(patientId, doctorId, monday, "10:00", "10:30");
        appointmentService.changeStatus(booked.id(), new UpdateStatusRequest(AppointmentStatus.CANCELLED, booked.version()));

        assertThat(doctorScheduleService.getSlots(doctorId, monday).freeSlots()).isEqualTo(6);
    }

    @Test
    void slotsExplainWhyThereAreNone() {
        assertThat(doctorScheduleService.getSlots(doctorId, monday).dayStatus()).isEqualTo("NO_TIMETABLE");

        giveDoctorWeekdayMornings();
        assertThat(doctorScheduleService.getSlots(doctorId, monday.plusDays(6)).dayStatus()).isEqualTo("DAY_OFF");

        doctorScheduleService.addLeave(doctorId, new LeaveRequest(monday, monday, "Training"));
        DoctorSlotsResponse onLeave = doctorScheduleService.getSlots(doctorId, monday);
        assertThat(onLeave.dayStatus()).isEqualTo("ON_LEAVE");
        assertThat(onLeave.slots()).isEmpty();
    }

    @Test
    void slotsOfAPastDateAreAllPast() {
        doctorScheduleService.replaceWorkingHours(doctorId, new WeeklyScheduleRequest(
                java.util.Arrays.stream(DayOfWeek.values())
                        .map(day -> new WorkingHoursRequest(day, LocalTime.of(9, 0), LocalTime.of(10, 0), 30))
                        .toList()));

        DoctorSlotsResponse yesterday = doctorScheduleService.getSlots(doctorId, LocalDate.now().minusDays(1));

        assertThat(yesterday.slots()).hasSize(2).allMatch(slot -> slot.status().equals("PAST"));
        assertThat(yesterday.freeSlots()).isZero();
    }
}
