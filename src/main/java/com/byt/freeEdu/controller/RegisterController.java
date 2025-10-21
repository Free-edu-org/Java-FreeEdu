package com.byt.freeEdu.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import com.byt.freeEdu.model.DTO.Auth.ApiResponse;
import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.service.users.UserService;

@RestController
@RequestMapping("/api/auth")
public class RegisterController{

  private final UserService userService;

  public RegisterController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/register")
  public Mono<ApiResponse> register(@RequestBody UserDto newUser) {
    userService.addUser(newUser);
    return Mono.just(new ApiResponse("OK", "Registered"));
  }
}
