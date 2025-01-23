package com.byt.freeEdu.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class WebfluxSecurityConfig {
    private final WebfluxSecurity webfluxSecurity;

    public WebfluxSecurityConfig(
            WebfluxSecurity webfluxSecurity
    ) {
        this.webfluxSecurity = webfluxSecurity;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return webfluxSecurity.enforcingAuthenticationWithoutCSRF(http);
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return webfluxSecurity.noOpEncoderAuthenticationManager();
    }
}
