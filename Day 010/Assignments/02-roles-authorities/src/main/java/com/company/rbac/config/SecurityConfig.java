package com.company.rbac.config;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAnyRole;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAuthority;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import com.company.rbac.security.JsonSecurityErrorHandlers;

/**
 * Endpoint authorization with roles and authorities.
 *
 *   hasRole("ADMIN")              checks for the authority "ROLE_ADMIN" (the prefix is added for us)
 *   hasAuthority("EMPLOYEE_READ") checks for exactly "EMPLOYEE_READ"
 *
 * Roles answer "who is this?" (coarse areas: admin pages, HR reports, team view).
 * Permissions answer "may they do this?" (fine-grained CRUD on employee records).
 *
 * The rules are checked top to bottom and the FIRST match decides, so the
 * specific rules come before the general ones.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain apiChain(HttpSecurity http, JsonSecurityErrorHandlers errors) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/error").permitAll()

                // ---- role-based areas (hasRole / hasAnyRole) ----
                .requestMatchers("/api/admin/**").hasRole("ADMIN")                    // users and roles
                .requestMatchers("/api/hr/**").hasAnyRole("HR", "ADMIN")              // the ADMIN+HR endpoint

                // MATCHER ORDER: /api/employees/team also matches the GET rule below, which every
                // USER passes. Listed first, only MANAGER and ADMIN reach the team view.
                .requestMatchers(HttpMethod.GET, "/api/employees/team").hasAnyRole("MANAGER", "ADMIN")

                // ---- permission-based CRUD (hasAuthority) ----
                .requestMatchers(HttpMethod.GET, "/api/employees", "/api/employees/*").hasAuthority("EMPLOYEE_READ")
                .requestMatchers(HttpMethod.POST, "/api/employees").hasAuthority("EMPLOYEE_WRITE")
                .requestMatchers(HttpMethod.PUT, "/api/employees/*").hasAuthority("EMPLOYEE_WRITE")
                // role AND authority: a transfer is an HR decision, so the permission alone
                // (held by the hr-sync integration account) is not enough
                .requestMatchers(HttpMethod.PATCH, "/api/employees/*/department").access(hrDecisionWithWriteAccess())
                .requestMatchers(HttpMethod.DELETE, "/api/employees/*").hasAuthority("EMPLOYEE_DELETE")

                // DENY BY DEFAULT: any other method or path under /api/employees is refused,
                // so a new endpoint is closed until somebody writes a rule for it
                .requestMatchers("/api/employees/**").denyAll()
                .anyRequest().authenticated())
            .httpBasic(basic -> basic.authenticationEntryPoint(errors))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())                  // stateless API: no session cookie to forge
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(errors)          // 401: who are you?
                .accessDeniedHandler(errors));             // 403: we know you, but no
        return http.build();
    }

    /** (ROLE_HR or ROLE_ADMIN) AND EMPLOYEE_WRITE: both checks must pass. */
    private static AuthorizationManager<RequestAuthorizationContext> hrDecisionWithWriteAccess() {
        return AuthorizationManagers.allOf(
                hasAnyRole("HR", "ADMIN"),
                hasAuthority("EMPLOYEE_WRITE"));
    }
}
