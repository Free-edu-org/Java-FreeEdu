package com.byt.freeEdu.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class Security{

  private final UserDetailsService userDetailsService;

  public Security(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }

  public ReactiveAuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
    ReactiveUserDetailsService reactiveUsers = username -> Mono
        .fromCallable(() -> userDetailsService.loadUserByUsername(username))
        .subscribeOn(Schedulers.boundedElastic()); // adapter na blokujące I/O

    UserDetailsRepositoryReactiveAuthenticationManager mgr = new UserDetailsRepositoryReactiveAuthenticationManager(
        reactiveUsers);
    mgr.setPasswordEncoder(passwordEncoder);
    return mgr;
  }

  public SecurityWebFilterChain enforcingAuthenticationWithoutCSRF(ServerHttpSecurity http) {
    return http.csrf(ServerHttpSecurity.CsrfSpec::disable).authorizeExchange(ex -> ex
        .pathMatchers("/","/index.html","/css/**","/js/**","/images/**","/assets/**","/favicon.ico")
        .permitAll().pathMatchers("/api/auth/login","/api/auth/register","/api/auth/me").permitAll()
        .pathMatchers("/api/auth/logout").authenticated()

        .pathMatchers("/api/admin/**").hasRole("ADMIN").pathMatchers("/api/teacher/**")
        .hasRole("TEACHER").pathMatchers("/api/parent/**").hasRole("PARENT")
        .pathMatchers("/api/student/**").hasRole("STUDENT").pathMatchers("/admin/**")
        .hasRole("ADMIN").pathMatchers("/teacher/**").hasRole("TEACHER").pathMatchers("/parent/**")
        .hasRole("PARENT").pathMatchers("/student/**").hasRole("STUDENT")

        .anyExchange().authenticated()).httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
        .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
        .logout(ServerHttpSecurity.LogoutSpec::disable)
        .securityContextRepository(new WebSessionServerSecurityContextRepository()).build();
  }
}
