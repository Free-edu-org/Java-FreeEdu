package com.byt.freeEdu.service.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import com.byt.freeEdu.model.users.User;

@Service
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService{

  private static final Logger logger = LoggerFactory
      .getLogger(CustomReactiveUserDetailsService.class);

  private final UserService userService;

  public CustomReactiveUserDetailsService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public Mono<UserDetails> findByUsername(String identifier) {
    logger.info("Looking for user with username or email: {}",identifier);

    return Mono.defer(() -> {
      User user = userService.getUserByUsername(identifier);

      if (user == null) {
        user = userService.getUserByEmail(identifier);
      }

      if (user == null) {
        throw new UsernameNotFoundException("User not found with username or email: " + identifier);
      }

      return Mono.just(user);
    }).map(
        user -> org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
            .password(user.getPassword()).roles(user.getUserRole().name()).build())
        .doOnTerminate(() -> logger.info("Finished processing user with identifier: {}",identifier))
        .onErrorResume(ex -> {
          logger.error("User not found with identifier: {}",identifier,ex);

          return Mono.error(
              new UsernameNotFoundException("User not found with identifier: " + identifier, ex));
        });
  }
}
