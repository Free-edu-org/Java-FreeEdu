package com.byt.freeEdu.controller.NEW;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.byt.freeEdu.model.DTO.Auth.ApiResponse;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.users.UserService;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    private final UserService userService;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody User newUser) {
        userService.addUser(
                newUser.getUsername(),
                newUser.getFirstname(),
                newUser.getLastname(),
                newUser.getEmail(),
                newUser.getPassword()
        );
        return ResponseEntity.ok(new ApiResponse("OK", "Registered"));
    }
}
