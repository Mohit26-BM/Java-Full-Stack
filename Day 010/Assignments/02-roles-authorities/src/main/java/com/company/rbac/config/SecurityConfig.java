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

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain apiChain(HttpSecurity http, JsonSecurityErrorHandlers errors) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/error").permitAll()

                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/hr/**").hasAnyRole("HR", "ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/employees/team").hasAnyRole("MANAGER", "ADMIN")

                .requestMatchers(HttpMethod.GET, "/api/employees", "/api/employees/*").hasAuthority("EMPLOYEE_READ")
                .requestMatchers(HttpMethod.POST, "/api/employees").hasAuthority("EMPLOYEE_WRITE")
                .requestMatchers(HttpMethod.PUT, "/api/employees/*").hasAuthority("EMPLOYEE_WRITE")

                .requestMatchers(HttpMethod.PATCH, "/api/employees/*/department").access(hrDecisionWithWriteAccess())
                .requestMatchers(HttpMethod.DELETE, "/api/employees/*").hasAuthority("EMPLOYEE_DELETE")

                .requestMatchers("/api/employees/**").denyAll()
                .anyRequest().authenticated())
            .httpBasic(basic -> basic.authenticationEntryPoint(errors))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(errors)
                .accessDeniedHandler(errors));
        return http.build();
    }

    private static AuthorizationManager<RequestAuthorizationContext> hrDecisionWithWriteAccess() {
        return AuthorizationManagers.allOf(
                hasAnyRole("HR", "ADMIN"),
                hasAuthority("EMPLOYEE_WRITE"));
    }
}
