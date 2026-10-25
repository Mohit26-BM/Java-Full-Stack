package com.example.hospital.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.example.hospital.dto.AppointmentResponse;
import com.example.hospital.dto.UpdateStatusRequest;
import com.example.hospital.entity.Appointment;
import com.example.hospital.entity.AppointmentStatus;
import com.example.hospital.exception.AppointmentModifiedException;
import com.example.hospital.exception.DoctorUnavailableException;

/** Concurrent updates: optimistic locking (@Version) and concurrent booking (row lock). */
class ConcurrencyIntegrationTest extends IntegrationTestBase {

    private Long doctorId;
    private Long patientId;
    private Long secondPatientId;

    @BeforeEach
    void createReferenceData() {
        Long departmentId = createDepartment("Cardiology").id();
        doctorId = createDoctor("D001", departmentId, "800.00").id();
        patientId = createPatient("P1001").id();
        secondPatientId = createPatient("P1002").id();
    }

    /**
     * Section 18 at JPA level: two users load the same appointment (two separate
     * copies, both version 0). User A saves first -> version 1. User B saves its
     * stale copy -> Hibernate's "UPDATE ... WHERE version = 0" matches no row ->
     * exception. B's change does NOT silently overwrite A's.
     */
    @Test
    void optimisticLocking_preventsALostUpdate() {
        AppointmentResponse booked = book(patientId, doctorId, visitDate, "10:00", "10:30");

        Appointment copyOfUserA = appointmentRepository.findById(booked.id()).orElseThrow();
        Appointment copyOfUserB = appointmentRepository.findById(booked.id()).orElseThrow();
        assertThat(copyOfUserA.getVersion()).isZero();
        assertThat(copyOfUserB.getVersion()).isZero();

        copyOfUserA.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(copyOfUserA);                         // version 0 -> 1

        copyOfUserB.setStatus(AppointmentStatus.CANCELLED);
        assertThatThrownBy(() -> appointmentRepository.save(copyOfUserB))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);

        Appointment inDatabase = appointmentRepository.findById(booked.id()).orElseThrow();
        assertThat(inDatabase.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);   // A's change kept
        assertThat(inDatabase.getVersion()).isEqualTo(1L);
    }

    /** The same scenario through the service/API: the client sends the version it loaded. */
    @Test
    void secondReceptionist_withOldVersion_isRejected() {
        AppointmentResponse booked = book(patientId, doctorId, visitDate, "10:00", "10:30");

        // both receptionists loaded version 0
        appointmentService.changeStatus(booked.id(), new UpdateStatusRequest(AppointmentStatus.CONFIRMED, 0L));

        assertThatThrownBy(() -> appointmentService.changeStatus(booked.id(),
                new UpdateStatusRequest(AppointmentStatus.CANCELLED, 0L)))
                .isInstanceOf(AppointmentModifiedException.class);
        assertThat(appointmentRepository.findById(booked.id()).orElseThrow().getStatus())
                .isEqualTo(AppointmentStatus.CONFIRMED);
    }

    /**
     * Two receptionists book the SAME doctor and slot at the same moment, from
     * two threads. The doctor row lock + READ_COMMITTED let exactly one succeed.
     */
    @Test
    void twoSimultaneousBookingsOfTheSameSlot_onlyOneSucceeds() throws Exception {
        CountDownLatch startTogether = new CountDownLatch(1);
        Callable<String> receptionist1 = () -> {
            startTogether.await();
            return tryToBook(patientId);
        };
        Callable<String> receptionist2 = () -> {
            startTogether.await();
            return tryToBook(secondPatientId);
        };

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<String> result1 = executor.submit(receptionist1);
        Future<String> result2 = executor.submit(receptionist2);
        startTogether.countDown();                                  // release both threads at once

        List<String> results = new ArrayList<>();
        results.add(result1.get());
        results.add(result2.get());
        executor.shutdown();

        assertThat(results).containsExactlyInAnyOrder("BOOKED", "DOCTOR_UNAVAILABLE");
        assertThat(appointmentRepository.count()).isEqualTo(1);
    }

    private String tryToBook(Long patient) {
        try {
            book(patient, doctorId, visitDate, "10:00", "10:30");
            return "BOOKED";
        } catch (DoctorUnavailableException ex) {
            return "DOCTOR_UNAVAILABLE";
        }
    }
}
