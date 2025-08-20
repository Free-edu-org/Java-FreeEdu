package com.byt.freeEdu.service.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.byt.freeEdu.model.users.User;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        logger.info("Looking for user with username or email: {}", identifier);

        User user = userService.getUserByUsername(identifier);
        if (user == null) {
            user = userService.getUserByEmail(identifier);
        }
        if (user == null) {
            logger.error("User not found with identifier: {}", identifier);
            throw new UsernameNotFoundException("User not found with username or email: " + identifier);
        }

        logger.info("Finished processing user with identifier: {}", identifier);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getUserRole().name())
                .build();
    }
}
