package com.byt.freeEdu.security;

import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.users.CustomReactiveUserDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
                        .pathMatchers("/view/homepage", "/view/login", "/view/register").permitAll()
                        .pathMatchers("/view/admin/**").hasRole("ADMIN")
                        .pathMatchers("/view/teacher/**").hasRole("TEACHER")
                        .pathMatchers("/view/parent/**").hasRole("PARENT")
                        .pathMatchers("/view/student/**").hasRole("STUDENT")
                        .anyExchange().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/view/login")
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {

                            System.out.println("Logged in user: " + authentication.getPrincipal());
                            System.out.println("Authorities: " + authentication.getAuthorities());
                            System.out.println("Authentication successful. Principal: " + authentication.getPrincipal());
                            Object principal = authentication.getPrincipal();
                            String role;

                            if (principal instanceof org.springframework.security.core.userdetails.User) {
                                role = authentication.getAuthorities().stream()
                                        .map(GrantedAuthority::getAuthority)
                                        .findFirst()
                                        .orElse("");
                            } else if (principal instanceof User) {
                                role = "ROLE_" + ((User) principal).getUser_role().name();
                            } else {
                                role = "UNKNOWN_ROLE";
                            }

                            URI redirectUri;
                            switch (role) {
                                case "ROLE_ADMIN":
                                    redirectUri = URI.create("/view/admin/mainpage");
                                    break;
                                case "ROLE_TEACHER":
                                    redirectUri = URI.create("/view/teacher/mainpage");
                                    break;
                                case "ROLE_PARENT":
                                    redirectUri = URI.create("/view/parent/mainpage");
                                    break;
                                case "ROLE_STUDENT":
                                    redirectUri = URI.create("/view/student/mainpage");
                                    break;
                                case "UNKNOWN_ROLE":
                                    System.err.println("Nieznana rola użytkownika. Przekierowanie na stronę logowania.");
                                    redirectUri = URI.create("/view/login?error=roleNotFound");
                                    break;
                                default:
                                    redirectUri = URI.create("/view/login?error=roleNotFound");
                                    break;
                            }

                            webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            webFilterExchange.getExchange().getResponse().getHeaders().setLocation(redirectUri);

                            return Mono.empty();
                        })
                        .authenticationFailureHandler((webFilterExchange, exception) -> {
                            String errorParam = "badCredentials";

                            webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                            webFilterExchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/view/login?error=" + errorParam));

                            return Mono.empty();
                        })
                );

        return http.build();
    }

    public ReactiveAuthenticationManager noOpEncoderAuthenticationManager() {
        return createAuthenticationManager(new BCryptPasswordEncoder());
    }

    public UserDetailsRepositoryReactiveAuthenticationManager createAuthenticationManager(PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager manager =
                new UserDetailsRepositoryReactiveAuthenticationManager(customReactiveUserDetailsService);
        manager.setPasswordEncoder(passwordEncoder);

        return manager;
    }
}