package com.company.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import com.company.security.security.JsonSecurityErrorHandlers;

/**
 * Spring Security 6 style: no WebSecurityConfigurerAdapter. The configuration is
 * made of SecurityFilterChain BEANS.
 *
 * Every request goes through FilterChainProxy, which picks the FIRST chain whose
 * securityMatcher matches the URL, and runs only that chain. Inside a chain the
 * authorizeHttpRequests rules are also checked in order, first match wins.
 *
 *   @Order(1) apiChain     /api/**                 REST: HTTP Basic, stateless, CSRF off, JSON errors
 *   @Order(2) browserChain everything else         browser: form login, session, CSRF on
 *
 * Active unless the "defaults" profile is used (run with --spring.profiles.active=defaults
 * to see Spring Boot's DEFAULT security instead, as described in the README).
 */
@Configuration
@EnableWebSecurity
@Profile("!defaults")
public class SecurityConfig {

    /**
     * REST API chain.
     *
     * MATCHER ORDER MATTERS: /api/employees/count is public, but it ALSO matches
     * /api/employees/**. Because the rules are checked top to bottom and the first
     * match wins, the specific rule must come BEFORE the general one. Swapped, the
     * count would require a login, because /api/employees/** would match first.
     * (Spring Security 6 also refuses to start if anyRequest() is not the last rule.)
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiChain(HttpSecurity http, JsonSecurityErrorHandlers errors) throws Exception {
        http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/employees/count").permitAll()   // specific first ...
                .requestMatchers("/api/employees/**").authenticated()                   // ... general after
                .anyRequest().authenticated())                                           // always last
            // Credentials in every request (Authorization: Basic base64(user:password))
            .httpBasic(basic -> basic.authenticationEntryPoint(errors))
            // A REST client sends credentials every time, so no HTTP session is created
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // CSRF protects COOKIE-based sessions: a malicious site could make the browser send
            // the session cookie automatically. This chain uses no cookies or sessions, and the
            // Authorization header is never added by the browser on its own, so CSRF is disabled
            // here, and ONLY here.
            .csrf(csrf -> csrf.disable())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(errors)      // 401 JSON
                .accessDeniedHandler(errors));         // 403 JSON
        return http.build();
    }

    /**
     * Browser chain: /admin/** pages with a custom login page, session cookie and logout.
     * CSRF stays ON (the default): the login form and the logout form carry a hidden token.
     */
    @Bean
    @Order(2)
    public SecurityFilterChain browserChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/error", "/favicon.ico").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")                       // our own page (LoginController)
                .defaultSuccessUrl("/admin/dashboard", false)
                .failureUrl("/login?error"))               // custom failure: back to the page with a message
            .logout(logout -> logout
                .logoutUrl("/logout")                      // POST only, with the CSRF token
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID"))
            .httpBasic(Customizer.withDefaults());         // harmless here; lets curl reach the pages too
        return http.build();
    }
}
