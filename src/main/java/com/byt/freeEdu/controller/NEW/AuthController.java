package com.byt.freeEdu.controller.NEW;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import com.byt.freeEdu.model.DTO.Auth.ApiResponse;
import com.byt.freeEdu.model.DTO.Auth.LoginRequest;
import com.byt.freeEdu.model.DTO.Auth.UserInfo;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.users.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authManager, UserService userService) {
        this.authManager = authManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest req, HttpServletRequest request) {
        try {
            var auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username(), req.password())
            );

            // Zapis SecurityContext do sesji + zmiana ID sesji (session fixation protection)
            var context = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            org.springframework.security.core.context.SecurityContextHolder.setContext(context);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
            request.changeSessionId();

            return ResponseEntity.ok(new ApiResponse("OK", "Logged in"));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(new ApiResponse("ERROR", "Bad credentials"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfo> me(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        User u = userService.getUserByUsername(principal.getUsername());
        if (u == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(new UserInfo(u.getUserId(), u.getUsername(), "ROLE_" + u.getUserRole().name()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return ResponseEntity.ok(new ApiResponse("OK", "Logged out"));
    }
}
