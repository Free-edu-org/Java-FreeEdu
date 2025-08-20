package com.byt.freeEdu.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.users.UserService;

@Component
public class UserUtils {

    private final UserService userService;

    public UserUtils(UserService userService) {
        this.userService = userService;
    }

    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user in context");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            User user = userService.getUserByUsername(username);
            if (user == null) {
                throw new IllegalStateException("User not found by username: " + username);
            }
            return user.getUserId();
        } else if (principal instanceof User) {
            return ((User) principal).getUserId();
        }

        throw new IllegalStateException("Unknown principal type: " + principal.getClass().getName());
    }
}
