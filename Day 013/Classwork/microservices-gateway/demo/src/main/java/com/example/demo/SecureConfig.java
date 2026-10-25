package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration @Profile("secure")
public class SecureConfig {
  @Bean PasswordEncoder encoder() { return new BCryptPasswordEncoder(); }

  @Bean SecurityFilterChain chain(HttpSecurity http) throws Exception {
    http.csrf(c -> c.disable())
        .authorizeHttpRequests(a -> a
            .requestMatchers("/public").permitAll()
            .requestMatchers("/admin").hasRole("ADMIN")
            .anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults());
    return http.build();
  }

  @Bean UserDetailsService users(PasswordEncoder enc) {
    return new InMemoryUserDetailsManager(
      User.withUsername("alice").password(enc.encode("alice123")).roles("USER").build(),
      User.withUsername("admin").password(enc.encode("admin123")).roles("ADMIN").build());
  }
}