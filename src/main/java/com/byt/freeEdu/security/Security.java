package com.byt.freeEdu.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component
public class Security {

    private final UserDetailsService userDetailsService;

    public Security(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /** AuthenticationManager oparty o DAO + PasswordEncoder. */
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    /** Konfiguracja łańcucha filtrów (MVC/Servlet). */
    public SecurityFilterChain enforcingAuthenticationWithoutCSRF(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index.html",
                                "/css/**", "/js/**", "/images/**", "/assets/**", "/favicon.ico").permitAll()
                        .requestMatchers("/api/auth/login", "/api/auth/register", "/api/auth/me").permitAll()
                        .requestMatchers("/api/auth/logout").authenticated()

                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/view/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/parent/**").hasRole("PARENT")
                        .requestMatchers("/api/parent/**").hasRole("PARENT")
                        .requestMatchers("/view/student/**").hasRole("STUDENT")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }
}
