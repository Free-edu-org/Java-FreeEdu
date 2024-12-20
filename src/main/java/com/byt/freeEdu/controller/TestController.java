package com.byt.freeEdu.controller;

import com.byt.freeEdu.model.Remark;
import com.byt.freeEdu.model.enums.UserRole;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.RemarkService;
import com.byt.freeEdu.service.users.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
public class TestController {
    private final RemarkService remarkService;
    private final UserService userService;

    public TestController(
            RemarkService remarkService,
            UserService userService
    ) {
        this.remarkService = remarkService;
        this.userService = userService;
    }

    @GetMapping("/test/test")
    public Mono<String> login() {
        this.userService.deleteUserById(4);

        return Mono.just("login");
    }
}
