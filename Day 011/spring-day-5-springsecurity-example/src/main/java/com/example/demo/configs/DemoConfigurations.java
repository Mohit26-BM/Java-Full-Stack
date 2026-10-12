package com.example.demo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled=true, securedEnabled=true, jsr250Enabled=true)
public class DemoConfigurations {

	@Bean
	SecurityFilterChain customSecurityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(
				requests ->requests
				.requestMatchers(HttpMethod.GET,"/bank/info", "/bank/register").permitAll()
				.requestMatchers("/bank/update", "/bank/deleteAccount").hasAnyAuthority("admin")
				.requestMatchers("/","/bank/balance").hasAnyAuthority("user","admin")
			
				.anyRequest()
				.authenticated());
				
		http.formLogin(Customizer.withDefaults());
		http.httpBasic(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
	
		UserDetails user1 = User.withUsername("satish")
				                .password("kumar")
				                .authorities("admin")
				                .build();

		UserDetails user2 = User.withUsername("praveen")
				                 .password("kumar")
				                 .authorities("user")
				                 .build();
		
		UserDetails user3 = User.withUsername("john")
				                .password("doe")
				                .authorities("manager")
				                .build();
		
		return new InMemoryUserDetailsManager(user1,user2,user3);

	}
	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
	        return (web) -> web.ignoring().requestMatchers("/ignore1", "/ignore2");
	}

	
	@Bean
	public PasswordEncoder encoder() {
	    return NoOpPasswordEncoder.getInstance();
	}

}















