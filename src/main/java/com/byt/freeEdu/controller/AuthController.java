package com.byt.freeEdu.controller;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import com.byt.freeEdu.model.DTO.Auth.ApiResponse;
import com.byt.freeEdu.model.DTO.Auth.LoginRequest;
import com.byt.freeEdu.model.DTO.Auth.UserInfo;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.users.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController{

  private final ReactiveAuthenticationManager authManager;

  private final UserService userService;

  private final ServerSecurityContextRepository contextRepo = new WebSessionServerSecurityContextRepository();

  public AuthController(ReactiveAuthenticationManager authManager, UserService userService) {
    this.authManager = authManager;
    this.userService = userService;
  }

  @PostMapping("/login")
  public Mono<ApiResponse> login(@RequestBody LoginRequest req,
      org.springframework.web.server.ServerWebExchange exchange) {
    return authManager
        .authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()))
        .flatMap(auth -> {
          SecurityContext context = new SecurityContextImpl(auth);
          return contextRepo.save(exchange,context)
              .then(Mono.just(new ApiResponse("OK", "Logged in")));
        }).onErrorResume(BadCredentialsException.class,
            ex -> Mono.just(new ApiResponse("ERROR", "Bad credentials")));
  }

  @GetMapping("/me")
  public Mono<UserInfo> me(@AuthenticationPrincipal Mono<UserDetails> principalMono) {
    return principalMono.flatMap(principal -> {
      User u = userService.getUserByUsername(principal.getUsername());
      return u != null
          ? Mono
              .just(new UserInfo(u.getUserId(), u.getUsername(), "ROLE_" + u.getUserRole().name()))
          : Mono.empty();
    }).switchIfEmpty(Mono.empty());
  }

  @PostMapping("/logout")
  public Mono<ApiResponse> logout(org.springframework.web.server.ServerWebExchange exchange) {
    return exchange.getSession().flatMap(session -> {
      session.invalidate();
      return Mono.just(new ApiResponse("OK", "Logged out"));
    });
  }
}