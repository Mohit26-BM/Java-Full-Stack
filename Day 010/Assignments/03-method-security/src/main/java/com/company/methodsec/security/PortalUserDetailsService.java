package com.company.methodsec.security;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.company.methodsec.config.PortalUsersProperties;

/**
 * A custom UserDetailsService holding explicit IN-MEMORY users.
 *
 * Spring Security calls loadUserByUsername() during every login (form or
 * HTTP Basic). It returns the stored hash and roles; Spring Security itself
 * then checks the typed password against the hash with the PasswordEncoder.
 * This class never sees or compares a plain password.
 *
 * (Spring's own InMemoryUserDetailsManager would work too; writing the service
 * shows exactly what Spring Security needs: username, hash, authorities, and
 * the account flags enabled/locked/expired.)
 */
@Service
public class PortalUserDetailsService implements UserDetailsService {

    private final Map<String, UserDetails> users;

    public PortalUserDetailsService(PortalUsersProperties properties) {
        // Built once at startup and never changed: an immutable, thread-safe map.
        this.users = properties.users().stream()
                .map(entry -> User.withUsername(entry.username())
                        .password(entry.passwordHash())
                        // roles("ADMIN") stores the authority "ROLE_ADMIN"
                        .roles(entry.roles().toArray(String[]::new))
                        .build())
                .collect(Collectors.toUnmodifiableMap(UserDetails::getUsername, Function.identity()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDetails user = users.get(username);
        if (user == null) {
            // The message is not shown to the client: Spring Security turns it into the
            // same "Bad credentials" as a wrong password, so attackers cannot tell which
            // usernames exist.
            throw new UsernameNotFoundException("No user " + username);
        }
        // Return a COPY: Spring Security erases the password of the returned object after
        // a successful login, which must not wipe the stored hash.
        return User.withUserDetails(user).build();
    }
}
