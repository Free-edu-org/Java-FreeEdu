package com.byt.freeEdu.controller.OLD.userSesion;

import org.springframework.stereotype.Service;

import com.byt.freeEdu.security.UserUtils;

@Service
public class SessionService{

  private final UserUtils userUtils;

  public SessionService(UserUtils userUtils) {
    this.userUtils = userUtils;
  }

  public Integer getUserId() {
    return userUtils.getCurrentUserId();
  }
}
