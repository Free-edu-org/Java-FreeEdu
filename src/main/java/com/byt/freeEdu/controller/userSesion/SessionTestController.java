package com.byt.freeEdu.controller.userSesion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SessionTestController {
    private final SessionService sessionService;

    public SessionTestController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/debug-user")
    public Mono<String> debugUser() {
        return sessionService.getUserId()
                .map(userId -> "Logged in user ID: " + userId)
                .onErrorResume(e -> Mono.just("Error: " + e.getMessage()));
    }
}
