package com.example.portal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * ALL security rules of the application, in one place.
 *
 * Endpoint access matrix:
 *
 *   endpoint                           anonymous  USER  HR   ADMIN
 *   POST   /api/auth/login, /refresh      yes      yes   yes   yes
 *   GET    /api/employees, /{id}          401      yes   yes   yes
 *   GET    /api/employees/{id}/payroll    401      403   yes   yes
 *   POST   /api/employees                 401      403   yes   yes
 *   PUT    /api/employees/{id}            401      403   yes   yes
 *   DELETE /api/employees/{id}            401      403   403   yes
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationConverter jwtAuthenticationConverter,
                                                   JsonAuthenticationEntryPoint authenticationEntryPoint,
                                                   JsonAccessDeniedHandler accessDeniedHandler) throws Exception {
        http
            // A stateless JSON API: the client sends a token in a header, no session cookie.
            // CSRF attacks rely on cookies the browser sends automatically, so CSRF protection is not needed.
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // AUTHORIZATION: which role may call which endpoint (first matching rule wins)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/refresh").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/employees/*/payroll").hasAnyRole("HR", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/employees", "/api/employees/*").hasAnyRole("USER", "HR", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/employees").hasAnyRole("HR", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/employees/*").hasAnyRole("HR", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/employees/*").hasRole("ADMIN")
                .anyRequest().denyAll())

            // AUTHENTICATION: read "Authorization: Bearer <token>" and validate the JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                .authenticationEntryPoint(authenticationEntryPoint))

            // JSON bodies for 401 and 403
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }

    /** Spring Security validates every incoming access token with this decoder. */
    @Bean
    public JwtDecoder jwtDecoder(JwtService jwtService) {
        return jwtService.getAccessTokenDecoder();
    }

    /**
     * Turns the token's "roles" claim ["HR"] into the authority ROLE_HR,
     * which is what hasRole("HR") checks.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthoritiesClaimName("roles");
        rolesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(rolesConverter);
        return converter;
    }

    /** Passwords are stored as BCrypt hashes, never as plain text. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Three demo users, one per role, kept in memory.
     * (A real application would load users from a database or an identity provider.)
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("user").password(passwordEncoder.encode("user123")).roles("USER").build(),
                User.withUsername("hr").password(passwordEncoder.encode("hr123")).roles("HR").build(),
                User.withUsername("admin").password(passwordEncoder.encode("admin123")).roles("ADMIN").build());
    }

    /** Used by the login endpoint to check username + password. */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
}
