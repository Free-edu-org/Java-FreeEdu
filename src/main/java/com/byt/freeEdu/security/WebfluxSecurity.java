package com.byt.freeEdu.security;

import com.byt.freeEdu.service.users.CustomReactiveUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class WebfluxSecurity {
    private final CustomReactiveUserDetailsService customReactiveUserDetailsService;

    public WebfluxSecurity(
            CustomReactiveUserDetailsService customReactiveUserDetailsService
    ) {
        this.customReactiveUserDetailsService = customReactiveUserDetailsService;
    }

    public SecurityWebFilterChain enforcingAuthenticationWithoutCSRF(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/view/login", "/register").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/view/login")
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            webFilterExchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/view/mainpage/student"));

                            return Mono.empty();
                        })
                        .authenticationFailureHandler((webFilterExchange, exception) -> {
                            webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            webFilterExchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/view/login?error"));

                            return Mono.empty();
                        })
                );

        return http.build();
    }

    public ReactiveAuthenticationManager noOpEncoderAuthenticationManager() {
        return createAuthenticationManager(NoOpPasswordEncoder.getInstance());
    }

    public UserDetailsRepositoryReactiveAuthenticationManager createAuthenticationManager(PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(customReactiveUserDetailsService);
        manager.setPasswordEncoder(passwordEncoder);

        return manager;
    }
}