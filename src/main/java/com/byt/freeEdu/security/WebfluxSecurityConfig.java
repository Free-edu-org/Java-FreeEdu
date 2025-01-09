package com.byt.freeEdu.security;

import com.byt.freeEdu.service.users.CustomReactiveUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class WebfluxSecurityConfig {

    private final CustomReactiveUserDetailsService customReactiveUserDetailsService;

    public WebfluxSecurityConfig(CustomReactiveUserDetailsService customReactiveUserDetailsService) {
        this.customReactiveUserDetailsService = customReactiveUserDetailsService;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable()) // Wyłączenie CSRF
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/view/login", "/register").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/view/login")
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            webFilterExchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/view/homepage"));
                            return Mono.empty();
                        })
                        .authenticationFailureHandler((webFilterExchange, exception) -> {
                            webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            webFilterExchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/custom-login?error"));
                            return Mono.empty();
                        })
                );
        return http.build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(customReactiveUserDetailsService);
        manager.setPasswordEncoder(passwordEncoder());
        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // Uwaga: Niebezpieczne, tylko do testów #todo
    }
}
