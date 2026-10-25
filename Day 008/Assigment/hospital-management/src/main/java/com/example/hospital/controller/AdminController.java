package com.example.hospital.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hospital.dto.NoShowRunResponse;
import com.example.hospital.dto.NotificationResponse;
import com.example.hospital.event.NotificationService;
import com.example.hospital.service.MissedAppointmentJob;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/** Extension: run the missed-appointment job on demand, and see the notifications that were sent. */
@Tag(name = "Admin", description = "Missed-appointment job and sent notifications")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final MissedAppointmentJob missedAppointmentJob;
    private final NotificationService notificationService;

    public AdminController(MissedAppointmentJob missedAppointmentJob, NotificationService notificationService) {
        this.missedAppointmentJob = missedAppointmentJob;
        this.notificationService = notificationService;
    }

    @Operation(summary = "Run the NO_SHOW job now (it also runs automatically every hour)")
    @PostMapping("/no-show-check")
    public NoShowRunResponse runNoShowCheck() {
        return missedAppointmentJob.runNow();
    }

    @Operation(summary = "Notifications sent since the application started, most recent first")
    @GetMapping("/notifications")
    public List<NotificationResponse> notifications() {
        return notificationService.recentNotifications();
    }
}
