package com.byt.freeEdu.controller;

import com.byt.freeEdu.controller.userSesion.SessionService;
import com.byt.freeEdu.mapper.UserMapper;
import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.users.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/view/parent")
public class ViewControllerParent {

    private final UserService userService;
    private final UserMapper userMapper;
    private final SessionService sessionService;

    public ViewControllerParent(UserService userService, UserMapper userMapper, SessionService sessionService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.sessionService = sessionService;
    }

    @GetMapping("/mainpage")
    public Mono<String> mainpageParent(Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    User user = userService.getUserById(userId);
                    UserDto userDto = userMapper.toDto(user);
                    model.addAttribute("user", userDto);
                    return Mono.just("parent/parent_mainpage");
                });
    }
}



