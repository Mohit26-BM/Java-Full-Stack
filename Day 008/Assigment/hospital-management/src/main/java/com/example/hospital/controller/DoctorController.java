package com.example.hospital.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.DoctorRequest;
import com.example.hospital.dto.DoctorResponse;
import com.example.hospital.dto.DoctorSummary;
import com.example.hospital.dto.DoctorSlotsResponse;
import com.example.hospital.dto.DoctorUpdateRequest;
import com.example.hospital.dto.LeaveRequest;
import com.example.hospital.dto.LeaveResponse;
import com.example.hospital.dto.WeeklyScheduleRequest;
import com.example.hospital.dto.WorkingHoursResponse;
import com.example.hospital.service.AppointmentService;
import com.example.hospital.service.DoctorScheduleService;
import com.example.hospital.service.DoctorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/** Module B - /api/doctors */
@Tag(name = "Doctors", description = "Module B - doctors, schedules, working hours, leave and free slots")
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final DoctorScheduleService doctorScheduleService;

    public DoctorController(DoctorService doctorService, AppointmentService appointmentService,
                            DoctorScheduleService doctorScheduleService) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.doctorScheduleService = doctorScheduleService;
    }

    /** Register a doctor in a department -> 201 Created */
    @PostMapping
    public ResponseEntity<DoctorResponse> register(@Valid @RequestBody DoctorRequest request) {
        DoctorResponse created = doctorService.registerDoctor(request);
        return ResponseEntity.created(URI.create("/api/doctors/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public DoctorResponse findById(@PathVariable("id") Long id) {
        return doctorService.findById(id);
    }

    /** Update details and consultation fee. */
    @PutMapping("/{id}")
    public DoctorResponse update(@PathVariable("id") Long id, @Valid @RequestBody DoctorUpdateRequest request) {
        return doctorService.updateDoctor(id, request);
    }

    @PatchMapping("/{id}/activate")
    public DoctorResponse activate(@PathVariable("id") Long id) {
        return doctorService.activateDoctor(id);
    }

    @PatchMapping("/{id}/deactivate")
    public DoctorResponse deactivate(@PathVariable("id") Long id) {
        return doctorService.deactivateDoctor(id);
    }

    /**
     * GET /api/doctors                              -> active doctors
     * GET /api/doctors?specialization=Cardiology    -> by specialization
     * GET /api/doctors?departmentId=1               -> active doctors of a department
     * GET /api/doctors?minFee=500&maxFee=800        -> fee range
     */
    @GetMapping
    public List<DoctorResponse> search(@RequestParam(name = "specialization", required = false) String specialization,
                                       @RequestParam(name = "departmentId", required = false) Long departmentId,
                                       @RequestParam(name = "minFee", required = false) BigDecimal minFee,
                                       @RequestParam(name = "maxFee", required = false) BigDecimal maxFee) {
        return doctorService.searchDoctors(specialization, departmentId, minFee, maxFee);
    }

    /** Lightweight list (JPQL DTO projection): GET /api/doctors/summaries */
    @GetMapping("/summaries")
    public List<DoctorSummary> summaries() {
        return doctorService.findActiveDoctorSummaries();
    }

    /** Query 5: GET /api/doctors/available?specialization=Cardiology&date=2026-10-20&startTime=10:00&endTime=10:30 */
    @GetMapping("/available")
    public List<DoctorSummary> available(@RequestParam("specialization") String specialization,
                                         @RequestParam("date") LocalDate date,
                                         @RequestParam("startTime") LocalTime startTime,
                                         @RequestParam("endTime") LocalTime endTime) {
        return doctorService.findAvailableDoctors(specialization, date, startTime, endTime);
    }

    /** Query 1 - doctor schedule: GET /api/doctors/1/appointments?date=2026-10-05 */
    @GetMapping("/{id}/appointments")
    public List<AppointmentResponse> schedule(@PathVariable("id") Long id, @RequestParam("date") LocalDate date) {
        return appointmentService.getDoctorSchedule(id, date);
    }

    // ---------------- Extension: working hours, leave and slots ----------------

    @Operation(summary = "Weekly working hours of a doctor (empty list = no timetable)")
    @GetMapping("/{id}/working-hours")
    public List<WorkingHoursResponse> workingHours(@PathVariable("id") Long id) {
        return doctorScheduleService.getWorkingHours(id);
    }

    @Operation(summary = "Replace the doctor's weekly working hours; weekdays not listed are days off")
    @PutMapping("/{id}/working-hours")
    public List<WorkingHoursResponse> replaceWorkingHours(@PathVariable("id") Long id,
                                                          @Valid @RequestBody WeeklyScheduleRequest request) {
        return doctorScheduleService.replaceWorkingHours(id, request);
    }

    @Operation(summary = "Leave periods of a doctor, earliest first")
    @GetMapping("/{id}/leaves")
    public List<LeaveResponse> leaves(@PathVariable("id") Long id) {
        return doctorScheduleService.getLeaves(id);
    }

    @Operation(summary = "Add leave (inclusive dates). Rejected if it overlaps another leave or existing appointments")
    @PostMapping("/{id}/leaves")
    public ResponseEntity<LeaveResponse> addLeave(@PathVariable("id") Long id, @Valid @RequestBody LeaveRequest request) {
        LeaveResponse created = doctorScheduleService.addLeave(id, request);
        return ResponseEntity.created(URI.create("/api/doctors/" + id + "/leaves/" + created.id())).body(created);
    }

    @Operation(summary = "Remove a leave period")
    @DeleteMapping("/{id}/leaves/{leaveId}")
    public ResponseEntity<Void> deleteLeave(@PathVariable("id") Long id, @PathVariable("leaveId") Long leaveId) {
        doctorScheduleService.deleteLeave(id, leaveId);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/doctors/1/slots?date=2026-11-02 */
    @Operation(summary = "Generated time slots of a doctor for one date: FREE, BOOKED or PAST")
    @GetMapping("/{id}/slots")
    public DoctorSlotsResponse slots(@PathVariable("id") Long id, @RequestParam("date") LocalDate date) {
        return doctorScheduleService.getSlots(id, date);
    }
}
