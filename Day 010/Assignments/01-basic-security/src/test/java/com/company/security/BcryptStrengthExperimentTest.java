package com.company.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Experiment: how long does one BCrypt hash take at different strengths?
 * Each +1 should roughly double the time. The table is printed to the console
 * (the measured values are recorded in the README). No Spring context needed.
 */
class BcryptStrengthExperimentTest {

    @Test
    void eachStepOfStrengthDoublesTheWork() {
        Map<Integer, Long> millis = new LinkedHashMap<>();
        for (int strength : new int[] {8, 10, 12, 14}) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(strength);
            encoder.encode("warm-up");                          // let the JIT compile the code first
            long start = System.nanoTime();
            String hash = encoder.encode("Admin@123");
            millis.put(strength, (System.nanoTime() - start) / 1_000_000);

            assertThat(hash).startsWith(String.format("$2a$%02d$", strength));   // the cost is always 2 digits: $2a$08$
            assertThat(encoder.matches("Admin@123", hash)).isTrue();
        }

        System.out.println("BCrypt strength -> milliseconds for one hash");
        millis.forEach((strength, ms) -> System.out.printf("  %2d -> %5d ms%n", strength, ms));

        // 14 is 64 times the work of 8: it must be clearly slower, even on a busy machine
        assertThat(millis.get(14)).isGreaterThan(millis.get(8) * 8);
    }
}
