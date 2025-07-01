package com.byt.freeEdu.security;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.users.UserService;

@Component
public class UserUtils{

  private final UserService userService;

  public UserUtils(UserService userService) {
    this.userService = userService;
  }

  public Mono<Integer> getCurrentUserId() {
    return ReactiveSecurityContextHolder.getContext()
        .map(securityContext -> securityContext.getAuthentication())
        .filter(authentication -> authentication != null && authentication.isAuthenticated())
        .flatMap(authentication -> {
          Object principal = authentication.getPrincipal();
          if (principal instanceof org.springframework.security.core.userdetails.User) {
            String username = ((org.springframework.security.core.userdetails.User) principal)
                .getUsername();
            return Mono.fromCallable(() -> userService.getUserByUsername(username))
                .map(User::getUserId);
          } else if (principal instanceof User) {
            return Mono.just(((User) principal).getUserId());
          }
          return Mono.error(new IllegalStateException("Unknown principal type"));
        });
  }
}
