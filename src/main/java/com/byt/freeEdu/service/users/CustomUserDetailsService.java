package com.byt.freeEdu.service.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.byt.freeEdu.model.users.User;

@Service
public class CustomUserDetailsService implements UserDetailsService{

  private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

  private final UserService userService;

  public CustomUserDetailsService(UserService userService) {
    this.userService = userService;
  }

  @Override
  public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    log.info("Looking for user by identifier: {}",identifier);

    User user = userService.getUserByUsername(identifier);
    if (user == null)
      user = userService.getUserByEmail(identifier);
    if (user == null) {
      log.warn("User not found: {}",identifier);
      throw new UsernameNotFoundException("User not found: " + identifier);
    }

    return org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
        .password(user.getPassword()).roles(user.getUserRole().name()).build();
  }
}
