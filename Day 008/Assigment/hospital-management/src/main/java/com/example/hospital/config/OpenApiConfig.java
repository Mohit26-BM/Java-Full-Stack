package com.example.hospital.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

/**
 * Extension: OpenAPI documentation (springdoc).
 * springdoc reads the controllers, request DTOs and their validation annotations
 * and builds the document automatically:
 *   http://localhost:8080/v3/api-docs        (JSON)
 *   http://localhost:8080/swagger-ui.html    (try the API in the browser)
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hospitalOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Hospital Appointment & Patient Management API")
                .version("2.0 (Day 09)")
                .description("""
                        Departments, doctors, patients, appointment booking without overlaps, the appointment \
                        lifecycle, medical records and reports. Day 09 adds doctor working hours and leave, \
                        free-slot generation, a status-change audit trail, a missed-appointment job and \
                        booking/cancellation notifications.

                        Errors always use the same JSON: timestamp, status, errorCode, message, path."""));
    }
}
