package com.company.security.security;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.company.security.config.PortalUsersProperties;

@Service
@Profile("!defaults")
public class PortalUserDetailsService implements UserDetailsService {
    private final Map<String, UserDetails> users;

    public PortalUserDetailsService(PortalUsersProperties properties) {
        this.users = properties.users().stream()
                .map(entry -> User.withUsername(entry.username())
                        .password(entry.passwordHash())

                        .roles(entry.roles().toArray(String[]::new))
                        .build())
                .collect(Collectors.toUnmodifiableMap(UserDetails::getUsername, Function.identity()));
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDetails user = users.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("No user " + username);
        }

        return User.withUserDetails(user).build();
    }
}
