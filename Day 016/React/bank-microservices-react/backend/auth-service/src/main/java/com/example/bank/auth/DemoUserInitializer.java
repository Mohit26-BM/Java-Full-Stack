package com.example.bank.auth;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Demo logins, created only when the "dev" profile is active, so real deployments have no built-in account.
 * demo (ADMIN), teller (TELLER) and aarav (CUSTOMER linked to the sample customer 1). Passwords are in the root README.
 */
@Configuration
@Profile("dev")
public class DemoUserInitializer {
 @Bean CommandLineRunner demo(UserCredentialRepository repo,PasswordEncoder encoder){return args->{
  ensure(repo,encoder,"demo","Demo@123",Role.ADMIN,null);
  ensure(repo,encoder,"teller","Teller@123",Role.TELLER,null);
  ensure(repo,encoder,"aarav","Customer@123",Role.CUSTOMER,1L);
 };}
 /** Creates the user if missing; an existing demo user (for example from before roles existed) gets the intended role. */
 private void ensure(UserCredentialRepository repo,PasswordEncoder encoder,String username,String password,Role role,Long customerId){
  UserCredential u=repo.findByUsername(username).orElseGet(()->new UserCredential(username,encoder.encode(password)));
  u.setRole(role); u.setCustomerId(customerId); repo.save(u);
 }
}
