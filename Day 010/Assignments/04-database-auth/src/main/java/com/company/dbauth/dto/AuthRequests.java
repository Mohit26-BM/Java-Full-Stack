package com.company.dbauth.dto;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Request bodies, with their validation rules. */
public final class AuthRequests {

    /**
     * Password policy: 8 to 72 characters with a lower-case letter, an upper-case
     * letter, a digit and a symbol. 72 is BCrypt's limit: it only uses the first
     * 72 bytes, so a longer password would be silently cut.
     */
    public static final String PASSWORD_RULE = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,72}$";
    public static final String PASSWORD_MESSAGE =
            "must be 8-72 characters with an upper-case letter, a lower-case letter, a digit and a symbol";

    private AuthRequests() {
    }

    public record RegistrationRequest(
            @NotBlank @Pattern(regexp = "^[A-Za-z0-9._-]{3,30}$",
                    message = "must be 3-30 characters: letters, digits, dot, dash or underscore") String username,
            @NotBlank @Email @Size(max = 120) String email,
            @NotBlank @Size(max = 80) String fullName,
            @NotNull @Pattern(regexp = PASSWORD_RULE, message = PASSWORD_MESSAGE) String password) {

        @Override
        public String toString() {                     // never log a password
            return "RegistrationRequest[" + username + ", " + email + "]";
        }
    }

    public record PasswordChangeRequest(
            @NotBlank String currentPassword,
            @NotNull @Pattern(regexp = PASSWORD_RULE, message = PASSWORD_MESSAGE) String newPassword) {

        @Override
        public String toString() {
            return "PasswordChangeRequest[***]";
        }
    }

    public record ResetRequest(@NotBlank @Email String email) {
    }

    public record ResetConfirmRequest(
            @NotBlank String token,
            @NotNull @Pattern(regexp = PASSWORD_RULE, message = PASSWORD_MESSAGE) String newPassword) {

        @Override
        public String toString() {
            return "ResetConfirmRequest[***]";
        }
    }

    public record EnabledRequest(@NotNull Boolean enabled) {
    }

    public record RolesRequest(@NotEmpty Set<@NotBlank String> roles) {
    }
}
