package com.example.hospital.service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.hospital.dto.NoShowRunResponse;

/**
 * Extension: scheduled job that finds missed appointments.
 *
 * An appointment still BOOKED or CONFIRMED more than the grace period (default
 * 2 hours) after its end time was not attended, so it is marked NO_SHOW. That
 * also frees nothing (the slot is in the past) but keeps reports honest.
 *
 * Transactions: this class has NO @Transactional. It reads the ids, then calls
 * AppointmentService.markNoShowIfStillOpen once per appointment, so every
 * appointment is updated in its OWN transaction. One appointment that fails
 * (for example because a receptionist changed it at the same moment) is
 * skipped without undoing the others.
 *
 * The schedule (cron) only runs when scheduling is enabled, see SchedulingConfig.
 * POST /api/admin/no-show-check runs the same code on demand.
 */
@Component
public class MissedAppointmentJob {

    private static final Logger log = LoggerFactory.getLogger(MissedAppointmentJob.class);

    private final AppointmentService appointmentService;
    private final Clock clock;
    private final Duration gracePeriod;

    public MissedAppointmentJob(AppointmentService appointmentService, Clock clock,
                                @Value("${hospital.no-show.grace-period:PT2H}") Duration gracePeriod) {
        this.appointmentService = appointmentService;
        this.clock = clock;
        this.gracePeriod = gracePeriod;
    }

    /** Runs on the cron schedule from application.properties (hourly at minute 15 by default). */
    @Scheduled(cron = "${hospital.no-show.cron:0 15 * * * *}")
    public void scheduledRun() {
        NoShowRunResponse result = runNow();
        if (result.found() > 0) {
            log.info("NO_SHOW job: {} found, {} marked NO_SHOW, {} skipped: {}", result.found(), result.markedNoShow(),
                    result.skipped(), result.appointmentNumbers());
        }
    }

    public NoShowRunResponse runNow() {
        return run(LocalDateTime.now(clock).withNano(0));
    }

    /** The moment is a parameter so tests can run the job "at" any time. */
    public NoShowRunResponse run(LocalDateTime now) {
        LocalDateTime cutoff = now.minus(gracePeriod);
        String note = "Not completed within " + describe(gracePeriod) + " after the end time";

        List<Long> ids = appointmentService.findOpenAppointmentIdsEndedBefore(cutoff);
        List<String> marked = new ArrayList<>();
        int skipped = 0;
        for (Long id : ids) {
            try {
                Optional<String> number = appointmentService.markNoShowIfStillOpen(id, cutoff, note);
                number.ifPresent(marked::add);
            } catch (ObjectOptimisticLockingFailureException concurrentChange) {
                skipped++;
                log.info("NO_SHOW job: appointment {} was changed by someone else during the run; skipped", id);
            }
        }
        return new NoShowRunResponse(now, cutoff, ids.size(), marked.size(), skipped, marked);
    }

    private static String describe(Duration duration) {
        long minutes = duration.toMinutes();
        return minutes % 60 == 0 ? (minutes / 60) + " hour(s)" : minutes + " minute(s)";
    }
}
