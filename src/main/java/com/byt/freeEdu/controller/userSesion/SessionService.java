package com.byt.freeEdu.controller.userSesion;

import com.byt.freeEdu.security.UserUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SessionService{

  private final UserUtils userUtils;

  public SessionService(UserUtils userUtils) {
    this.userUtils = userUtils;
  }

  public Mono<Integer> getUserId() {
    return userUtils.getCurrentUserId();
  }
}
