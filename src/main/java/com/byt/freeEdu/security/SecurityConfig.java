package com.byt.freeEdu.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // opcjonalnie, jeśli używasz @PreAuthorize itp.
public class SecurityConfig {

  private final Security security;

  public SecurityConfig(Security security) {
    this.security = security;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
    return security.authenticationManager(passwordEncoder);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                       AuthenticationManager authManager)
          throws Exception {
    return security.enforcingAuthenticationWithoutCSRF(http, authManager);
  }
}
