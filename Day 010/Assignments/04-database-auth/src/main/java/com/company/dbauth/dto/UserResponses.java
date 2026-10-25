package com.company.dbauth.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.company.dbauth.entity.AppUser;
import com.company.dbauth.entity.Role;
import com.fasterxml.jackson.annotation.JsonInclude;

/** Response bodies. None of them contains a password hash. */
public final class UserResponses {

    private UserResponses() {
    }

    /** Returned by registration and the admin user list. */
    public record UserResponse(Long id, String username, String email, String fullName, boolean enabled,
                               boolean locked, int failedLoginAttempts, List<String> roles, LocalDateTime createdAt) {

        public static UserResponse of(AppUser user, LocalDateTime now) {
            return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getFullName(),
                    user.isEnabled(), user.isLockedAt(now), user.getFailedLoginAttempts(),
                    user.getRoles().stream().map(Role::getName).sorted().toList(), user.getCreatedAt());
        }
    }

    public record MeResponse(String username, String fullName, List<String> authorities) {
    }

    public record DirectoryEntry(String fullName, String email) {
    }

    /** token is present only when portal.security.return-reset-token=true (demo without e-mail). */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ResetRequested(String message, String token) {
    }
}
