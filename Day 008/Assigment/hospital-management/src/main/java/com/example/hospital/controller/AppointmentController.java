package com.example.hospital.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hospital.dto.AppointmentCompletionResponse;
import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.CompleteAppointmentRequest;
import com.example.hospital.dto.CreateAppointmentRequest;
import com.example.hospital.dto.PageResponse;
import com.example.hospital.dto.StatusChangeResponse;
import com.example.hospital.dto.UpdateStatusRequest;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/** Modules D and E - /api/appointments */
@Tag(name = "Appointments", description = "Booking, lifecycle, completion, paginated search and status history")
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /** Book an appointment -> 201 Created (status BOOKED) */
    @PostMapping
    public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody CreateAppointmentRequest request) {
        AppointmentResponse created = appointmentService.bookAppointment(request);
        return ResponseEntity.created(URI.create("/api/appointments/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public AppointmentResponse findById(@PathVariable("id") Long id) {
        return appointmentService.findById(id);
    }

    /**
     * Paginated search, every filter optional:
     * GET /api/appointments?page=0&size=10&sort=appointmentDate,desc
     * GET /api/appointments?doctorId=1&status=BOOKED&date=2026-10-20&departmentId=1&patientId=2
     * Spring fills the Pageable from page, size and sort (default: 10 per page, newest date first).
     */
    @GetMapping
    public PageResponse<AppointmentResponse> search(
            @RequestParam(name = "doctorId", required = false) Long doctorId,
            @RequestParam(name = "patientId", required = false) Long patientId,
            @RequestParam(name = "departmentId", required = false) Long departmentId,
            @RequestParam(name = "status", required = false) AppointmentStatus status,
            @RequestParam(name = "date", required = false) LocalDate date,
            @PageableDefault(size = 10, sort = "appointmentDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return appointmentService.searchAppointments(doctorId, patientId, departmentId, status, date, pageable);
    }

    /** Change the status: body {"status":"CONFIRMED","version":0} */
    @PatchMapping("/{id}/status")
    public AppointmentResponse changeStatus(@PathVariable("id") Long id,
                                            @Valid @RequestBody UpdateStatusRequest request) {
        return appointmentService.changeStatus(id, request);
    }

    /** Complete the appointment and create its medical record in one transaction. */
    @PostMapping("/{id}/complete")
    public AppointmentCompletionResponse complete(@PathVariable("id") Long id,
                                                  @Valid @RequestBody CompleteAppointmentRequest request) {
        return appointmentService.completeAppointment(id, request);
    }

    /** Extension: the audit trail, oldest change first. */
    @Operation(summary = "Status history (audit trail) of an appointment, oldest first")
    @GetMapping("/{id}/status-history")
    public List<StatusChangeResponse> statusHistory(@PathVariable("id") Long id) {
        return appointmentService.getStatusHistory(id);
    }
}
