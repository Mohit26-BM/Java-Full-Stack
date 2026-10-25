package com.company.dbauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.company.dbauth.security.JsonSecurityErrorHandlers;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain apiChain(HttpSecurity http, JsonSecurityErrorHandlers errors) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/error").permitAll()

                .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/password-reset/**").permitAll()

                .requestMatchers("/api/admin/**").hasAuthority("USER_MANAGE")
                .requestMatchers(HttpMethod.GET, "/api/directory").hasAuthority("EMPLOYEE_READ")
                .anyRequest().authenticated())
            .httpBasic(basic -> basic.authenticationEntryPoint(errors))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(errors)
                .accessDeniedHandler(errors));
        return http.build();
    }
}
