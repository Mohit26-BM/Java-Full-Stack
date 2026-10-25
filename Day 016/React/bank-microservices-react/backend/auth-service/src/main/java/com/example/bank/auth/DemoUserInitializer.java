package com.example.bank.auth;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration
public class DemoUserInitializer {
 @Bean CommandLineRunner demo(UserCredentialRepository repo,PasswordEncoder encoder){return args->{if(!repo.existsByUsername("demo"))repo.save(new UserCredential("demo",encoder.encode("Demo@123")));};}
}
