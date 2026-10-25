package com.example.hospital.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.hospital.service.AppointmentService;

/**
 * Web-layer test: only the controller, Bean Validation and the exception handler
 * are started (no database). Invalid requests must be answered with 400 BEFORE
 * the service is called.
 */
@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppointmentService appointmentService;

    @Test
    void pastAppointmentDate_returns400() throws Exception {
        String yesterday = LocalDate.now().minusDays(1).toString();
        String body = """
                {"patientId":1,"doctorId":1,"appointmentDate":"%s","startTime":"10:00","endTime":"10:30"}
                """.formatted(yesterday);

        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.validationErrors.appointmentDate").value("appointmentDate must not be in the past"))
                .andExpect(jsonPath("$.path").value("/api/appointments"));

        verify(appointmentService, never()).bookAppointment(any());
    }

    @Test
    void endTimeBeforeStartTime_returns400() throws Exception {
        String nextWeek = LocalDate.now().plusDays(7).toString();
        String body = """
                {"patientId":1,"doctorId":1,"appointmentDate":"%s","startTime":"11:00","endTime":"10:30"}
                """.formatted(nextWeek);

        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.endTimeAfterStartTime").value("endTime must be after startTime"));

        verify(appointmentService, never()).bookAppointment(any());
    }

    @Test
    void missingFields_return400_withEveryFieldListed() throws Exception {
        mockMvc.perform(post("/api/appointments").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.patientId").value("patientId is required"))
                .andExpect(jsonPath("$.validationErrors.doctorId").value("doctorId is required"))
                .andExpect(jsonPath("$.validationErrors.appointmentDate").value("appointmentDate is required"))
                .andExpect(jsonPath("$.validationErrors.startTime").value("startTime is required"))
                .andExpect(jsonPath("$.validationErrors.endTime").value("endTime is required"));
    }
}
