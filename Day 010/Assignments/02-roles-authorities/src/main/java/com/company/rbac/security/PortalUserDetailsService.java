package com.company.rbac.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.company.rbac.config.PortalUsersProperties;
import com.company.rbac.config.PortalUsersProperties.UserEntry;

/**
 * In-memory users with roles AND permissions.
 *
 * Spring Security only knows one concept: a list of GrantedAuthority strings.
 * A "role" is just an authority whose name starts with ROLE_. So for hana (HR,
 * may read and write) the list is:
 *
 *     ROLE_HR, EMPLOYEE_READ, EMPLOYEE_WRITE
 *
 * Careful: on User.builder(), roles(...) and authorities(...) both REPLACE the
 * whole list, so calling one after the other keeps only the last call. That is
 * why the list is built here and passed once to authorities(...).
 */
@Service
public class PortalUserDetailsService implements UserDetailsService {

    private final Map<String, UserDetails> users;
    private final Map<String, UserEntry> entries;

    public PortalUserDetailsService(PortalUsersProperties properties) {
        this.entries = properties.users().stream()
                .collect(Collectors.toUnmodifiableMap(UserEntry::username, Function.identity()));
        this.users = properties.users().stream()
                .map(entry -> User.withUsername(entry.username())
                        .password(entry.passwordHash())
                        .authorities(authoritiesOf(entry))
                        .build())
                .collect(Collectors.toUnmodifiableMap(UserDetails::getUsername, Function.identity()));
    }

    static Collection<GrantedAuthority> authoritiesOf(UserEntry entry) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        entry.roles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.authority())));   // ROLE_HR
        entry.permissions().forEach(p -> authorities.add(new SimpleGrantedAuthority(p.name())));        // EMPLOYEE_WRITE
        return authorities;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user " + username);   // shown to the client as "Bad credentials"
        }
        return User.withUserDetails(user).build();                        // a copy: the password gets erased after login
    }

    /** The employee record a login belongs to (empty for the integration account). */
    public Optional<Long> employeeIdOf(String username) {
        return Optional.ofNullable(entries.get(username)).map(UserEntry::employeeId);
    }

    /** All accounts, for the ADMIN user overview. */
    public List<UserEntry> allUsers() {
        return entries.values().stream()
                .sorted((a, b) -> a.username().compareTo(b.username()))
                .toList();
    }
}
