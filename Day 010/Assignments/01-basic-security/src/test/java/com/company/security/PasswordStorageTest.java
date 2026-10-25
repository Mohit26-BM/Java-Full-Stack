package com.company.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.company.security.config.PortalUsersProperties;
import com.company.security.config.PortalUsersProperties.UserEntry;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/** BCrypt: hashes only, salted, slow by design; plain-text passwords are rejected at startup. */
@SpringBootTest
class PasswordStorageTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PortalUsersProperties properties;

    @Test
    void theEncoderIsBCryptWithStrength12() {
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
        assertThat(passwordEncoder.encode("x")).startsWith("$2a$12$");
    }

    @Test
    void storedValuesAreHashes_thatMatchOnlyTheRightPassword() {
        String adminHash = properties.users().get(0).passwordHash();

        assertThat(adminHash).startsWith("$2a$12$").doesNotContain("Admin@123");
        assertThat(passwordEncoder.matches("Admin@123", adminHash)).isTrue();
        assertThat(passwordEncoder.matches("admin@123", adminHash)).isFalse();
    }

    /** The random salt: the same password never gives the same hash twice, yet both verify. */
    @Test
    void samePasswordGivesDifferentHashes_becauseOfTheSalt() {
        String first = passwordEncoder.encode("Alice@123");
        String second = passwordEncoder.encode("Alice@123");

        assertThat(first).isNotEqualTo(second);
        assertThat(passwordEncoder.matches("Alice@123", first)).isTrue();
        assertThat(passwordEncoder.matches("Alice@123", second)).isTrue();
    }

    @Test
    void aPlainTextPasswordInTheConfiguration_isRejected() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        PortalUsersProperties plain = new PortalUsersProperties(
                List.of(new UserEntry("bob", "Bob@123", List.of("USER"))));

        assertThat(validator.validate(plain))
                .extracting(violation -> violation.getMessage())
                .containsExactly("must be a BCrypt hash, not a plain password");
    }
}
