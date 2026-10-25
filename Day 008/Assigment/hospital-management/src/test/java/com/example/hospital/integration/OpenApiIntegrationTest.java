package com.example.hospital.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

/** Extension: the OpenAPI document is generated and lists the new endpoints. */
@AutoConfigureMockMvc
class OpenApiIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void apiDocsDescribeTheEndpoints() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Hospital Appointment & Patient Management API"))
                .andExpect(jsonPath("$.paths['/api/appointments'].post").exists())
                .andExpect(jsonPath("$.paths['/api/doctors/{id}/slots'].get").exists())
                .andExpect(jsonPath("$.paths['/api/doctors/{id}/working-hours'].put").exists())
                .andExpect(jsonPath("$.paths['/api/appointments/{id}/status-history'].get").exists())
                .andExpect(jsonPath("$.paths['/api/admin/no-show-check'].post").exists())
                // Bean Validation annotations become schema rules
                .andExpect(jsonPath("$.components.schemas.WorkingHoursRequest.properties.slotMinutes.minimum").value(5));
    }

    @Test
    void swaggerUiIsServed() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }
}
