package com.example.hospital.event;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.hospital.dto.NotificationResponse;
import com.example.hospital.entity.AppointmentStatus;

/**
 * Extension: sends the patient a notification when an appointment is booked
 * or cancelled.
 *
 * @TransactionalEventListener(AFTER_COMMIT) means the listener runs only if
 * the transaction that published the event COMMITS. If the booking is rolled
 * back, nothing is sent: a patient is never told about an appointment that
 * does not exist.
 *
 * "Sending" is simulated: the message is written to the log and the last 100
 * are kept in memory (GET /api/admin/notifications). A real system would call
 * an e-mail or SMS service here.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private static final int KEEP = 100;

    private final Deque<NotificationResponse> sent = new ConcurrentLinkedDeque<>();
    private final Clock clock;

    public NotificationService(Clock clock) {
        this.clock = clock;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBooked(AppointmentBookedEvent event) {
        send("BOOKING_CONFIRMATION", event.appointmentNumber(), recipient(event.patientEmail(), event.patientPhone()),
                "Dear " + event.patientName() + ", your appointment " + event.appointmentNumber() + " with "
                        + event.doctorName() + " is booked for " + event.date() + " at " + event.startTime() + ".");
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onStatusChanged(AppointmentStatusChangedEvent event) {
        if (event.toStatus() != AppointmentStatus.CANCELLED) {
            return;   // only cancellations are notified; other changes are visible in the status history
        }
        send("CANCELLATION", event.appointmentNumber(), recipient(event.patientEmail(), event.patientPhone()),
                "Dear " + event.patientName() + ", your appointment " + event.appointmentNumber() + " with "
                        + event.doctorName() + " on " + event.date() + " at " + event.startTime() + " has been cancelled.");
    }

    /** Most recent first. */
    public List<NotificationResponse> recentNotifications() {
        return new ArrayList<>(sent);
    }

    /** Used by tests to start from an empty list. */
    public void clear() {
        sent.clear();
    }

    private void send(String type, String appointmentNumber, String recipient, String message) {
        NotificationResponse notification = new NotificationResponse(LocalDateTime.now(clock).withNano(0), type,
                appointmentNumber, recipient, message);
        sent.addFirst(notification);
        while (sent.size() > KEEP) {
            sent.removeLast();
        }
        log.info("[NOTIFICATION] {} to {}: {}", type, recipient, message);
    }

    /** E-mail is optional for patients; the phone number is mandatory. */
    private static String recipient(String email, String phone) {
        return email != null ? email : "SMS " + phone;
    }
}
