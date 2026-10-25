package com.company.methodsec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.company.methodsec.security.JsonSecurityErrorHandlers;

/**
 * Two layers of security.
 *
 * 1. URL security (this filter chain): a COARSE gate at the door. It only knows
 *    the HTTP method and path: "is this person logged in?", "only ADMIN may send
 *    DELETE to /api/employees/**".
 *
 * 2. Method security (@EnableMethodSecurity): FINE-grained rules on the service
 *    methods themselves (EmployeeService). They see the method PARAMETERS and
 *    the RETURN value, and they apply however the method is reached: from any
 *    controller, a scheduled job or another service.
 *
 * prePostEnabled (the default) switches on @PreAuthorize / @PostAuthorize;
 * securedEnabled additionally switches on the older @Secured annotation.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain apiChain(HttpSecurity http, JsonSecurityErrorHandlers errors) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/error").permitAll()
                // defence in depth: deleting is ADMIN-only here AND on the service method
                .requestMatchers(HttpMethod.DELETE, "/api/employees/**").hasRole("ADMIN")
                // everything else: just "logged in"; the service methods decide the rest.
                // /api/tools/** (HrToolsController) deliberately has NO specific URL rule.
                .anyRequest().authenticated())
            .httpBasic(basic -> basic.authenticationEntryPoint(errors))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(errors)          // 401
                .accessDeniedHandler(errors));             // 403, also for denials thrown by method security
        return http.build();
    }
}
