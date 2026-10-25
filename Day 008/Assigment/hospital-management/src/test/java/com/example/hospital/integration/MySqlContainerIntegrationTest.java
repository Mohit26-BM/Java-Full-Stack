package com.example.hospital.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.entity.AppointmentStatus;

/**
 * Extension: the same application against a brand-new MySQL 8.4 started in
 * Docker by Testcontainers, instead of the locally installed server.
 *
 * @ServiceConnection replaces the datasource URL, user and password from
 * application.properties with the container's. Flyway builds the schema from
 * nothing, which proves the migrations work on a clean server.
 *
 * disabledWithoutDocker = true: when Docker is not running the class is
 * reported as SKIPPED instead of failing.
 */
@Testcontainers(disabledWithoutDocker = true)
class MySqlContainerIntegrationTest extends IntegrationTestBase {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void migrationsRunOnAFreshServer_andBookingWorks() {
        Integer migrations = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM flyway_schema_history WHERE success = 1 AND version IS NOT NULL", Integer.class);
        assertThat(migrations).isEqualTo(3);

        Long departmentId = createDepartment("Cardiology").id();
        Long doctorId = createDoctor("D001", departmentId, "800.00").id();
        Long patientId = createPatient("P1001").id();
        AppointmentResponse booked = book(patientId, doctorId, visitDate, "10:00", "10:30");

        assertThat(booked.status()).isEqualTo(AppointmentStatus.BOOKED);
    }
}
