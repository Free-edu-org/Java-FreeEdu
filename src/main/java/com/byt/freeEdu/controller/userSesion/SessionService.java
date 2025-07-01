package com.byt.freeEdu.controller.userSesion;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import com.byt.freeEdu.security.UserUtils;

@Service
public class SessionService {

    private final UserUtils userUtils;

    public SessionService(final UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    public Mono<Integer> getUserId() {
        return userUtils.getCurrentUserId();
    }
}
