package com.example.hospital.dto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response of GET /api/doctors/{id}/slots?date=...
 *
 * dayStatus says why there are (or are not) slots:
 *   WORKING      - slots generated from the doctor's hours for that weekday
 *   ON_LEAVE     - the doctor is on leave that day (no slots)
 *   DAY_OFF      - the timetable has no hours for that weekday (no slots)
 *   NO_TIMETABLE - the doctor has no timetable at all, so no slots can be generated
 *                  (bookings are still accepted at any time)
 *   INACTIVE     - the doctor is deactivated (no slots)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DoctorSlotsResponse(Long doctorId,
                                  String doctorCode,
                                  String doctorName,
                                  LocalDate date,
                                  DayOfWeek dayOfWeek,
                                  String dayStatus,
                                  String message,
                                  LocalTime workStart,
                                  LocalTime workEnd,
                                  Integer slotMinutes,
                                  long freeSlots,
                                  List<TimeSlot> slots) {
}
