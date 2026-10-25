package com.example.hospital.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import com.example.hospital.service.DoctorScheduleService;
import com.example.hospital.service.DoctorService;

/** Web-layer validation of the Day 09 doctor endpoints (no database). */
@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;
    @MockitoBean
    private AppointmentService appointmentService;
    @MockitoBean
    private DoctorScheduleService doctorScheduleService;

    @Test
    void leaveEndingBeforeItStarts_returns400() throws Exception {
        String start = LocalDate.now().plusDays(10).toString();
        String end = LocalDate.now().plusDays(8).toString();

        mockMvc.perform(post("/api/doctors/1/leaves").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"startDate":"%s","endDate":"%s","reason":"Holiday"}
                                """.formatted(start, end)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.endDateOnOrAfterStartDate")
                        .value("endDate must be on or after startDate"));

        verify(doctorScheduleService, never()).addLeave(anyLong(), any());
    }

    @Test
    void workingHoursWithTooShortSlotsOrEndBeforeStart_returns400() throws Exception {
        mockMvc.perform(put("/api/doctors/1/working-hours").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"days":[{"dayOfWeek":"MONDAY","startTime":"12:00","endTime":"09:00","slotMinutes":2}]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors['days[0].slotMinutes']").value("slotMinutes must be at least 5"))
                .andExpect(jsonPath("$.validationErrors['days[0].endTimeAfterStartTime']").value("endTime must be after startTime"));

        verify(doctorScheduleService, never()).replaceWorkingHours(anyLong(), any());
    }

    @Test
    void unknownWeekday_returns400MalformedRequest() throws Exception {
        mockMvc.perform(put("/api/doctors/1/working-hours").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"days":[{"dayOfWeek":"FUNDAY","startTime":"09:00","endTime":"12:00","slotMinutes":30}]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("MALFORMED_REQUEST"));
    }
}
