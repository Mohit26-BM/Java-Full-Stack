package com.example.bank.auth;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
@Configuration
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
  http.csrf(c->c.disable()).cors(c->c.configurationSource(req->{CorsConfiguration x=new CorsConfiguration();x.setAllowedOrigins(List.of("http://localhost:5173","http://127.0.0.1:5173"));x.setAllowedMethods(List.of("GET","POST","OPTIONS"));x.setAllowedHeaders(List.of("*"));x.setAllowCredentials(false);return x;}))
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a.requestMatchers("/auth/**","/error","/actuator/health/**").permitAll().anyRequest().authenticated())
   .httpBasic(b->b.disable()).formLogin(f->f.disable());
  return http.build();
 }
}
