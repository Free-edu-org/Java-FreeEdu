package com.byt.freeEdu.controller.userSesion;


import com.byt.freeEdu.security.UserUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class UserSessionController {

    private final UserUtils userUtils;

    public UserSessionController(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @GetMapping("/current-user-id")
    public Mono<Integer> getCurrentUserId() {
        return userUtils.getCurrentUserId();
    }
}
