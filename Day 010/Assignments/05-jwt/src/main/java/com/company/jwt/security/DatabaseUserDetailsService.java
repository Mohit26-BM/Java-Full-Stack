package com.company.jwt.security;

import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.jwt.entity.AppUser;
import com.company.jwt.entity.Permission;
import com.company.jwt.entity.Role;
import com.company.jwt.repository.AppUserRepository;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final AppUserRepository users;

    public DatabaseUserDetailsService(AppUserRepository users) {
        this.users = users;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        AppUser user = users.findWithAuthoritiesByUsername(username.toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new UsernameNotFoundException("No user " + username));
        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authoritiesOf(user).toArray(String[]::new))
                .disabled(!user.isEnabled())
                .build();
    }

    public static List<String> authoritiesOf(AppUser user) {
        TreeSet<String> permissions = new TreeSet<>();
        TreeSet<String> roles = new TreeSet<>();
        for (Role role : user.getRoles()) {
            roles.add("ROLE_" + role.getName());
            role.getPermissions().stream().map(Permission::getName).forEach(permissions::add);
        }
        List<String> all = new java.util.ArrayList<>(permissions);
        all.addAll(roles);
        return all;
    }
}
