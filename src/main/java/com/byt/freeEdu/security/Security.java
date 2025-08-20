package com.byt.freeEdu.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
public class Security {

    private final UserDetailsService userDetailsService;

    public Security(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Tworzy AuthenticationManager (DAO) z podanym PasswordEncoderem.
     */
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    /**
     * Konfiguracja łańcucha filtrów dla MVC (Servlet).
     */
    public SecurityFilterChain enforcingAuthenticationWithoutCSRF(HttpSecurity http,
                                                                  AuthenticationManager authManager)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authenticationManager(authManager)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/view/homepage", "/view/login", "/view/register").permitAll()
                        .requestMatchers("/view/admin/**").hasRole("ADMIN")
                        .requestMatchers("/view/teacher/**").hasRole("TEACHER")
                        .requestMatchers("/view/parent/**").hasRole("PARENT")
                        .requestMatchers("/view/student/**").hasRole("STUDENT")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/view/login")
                        .successHandler(this::onAuthSuccess)
                        .failureHandler(this::onAuthFailure)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/view/login?logout")
                );

        return http.build();
    }

    private void onAuthSuccess(HttpServletRequest request,
                               HttpServletResponse response,
                               Authentication authentication) throws IOException, ServletException {

        // Spróbuj znaleźć pierwszą authority zaczynającą się od "ROLE_"
        String role = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a != null && a.startsWith("ROLE_"))
                .findFirst()
                .orElse("UNKNOWN_ROLE");

        String target = switch (role) {
            case "ROLE_ADMIN" -> "/view/admin/mainpage";
            case "ROLE_TEACHER" -> "/view/teacher/mainpage";
            case "ROLE_PARENT" -> "/view/parent/mainpage";
            case "ROLE_STUDENT" -> "/view/student/mainpage";
            default -> "/view/login?error=roleNotFound";
        };

        response.sendRedirect(target);
    }

    private void onAuthFailure(HttpServletRequest request,
                               HttpServletResponse response,
                               Exception exception) throws IOException {
        // Możesz rozróżniać typy wyjątków i dawać różne parametry
        String errorParam = "badCredentials";
        response.sendRedirect("/view/login?error=" + errorParam);
    }
}
