package com.byt.freeEdu.controller;

import com.byt.freeEdu.controller.userSesion.SessionService;
import com.byt.freeEdu.mapper.UserMapper;
import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.ScheduleService;
import com.byt.freeEdu.service.users.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/view/admin")
public class ViewControllerAdmin {

    private final SessionService sessionService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ScheduleService scheduleService;

    public ViewControllerAdmin(SessionService sessionService, UserService userService, UserMapper userMapper, ScheduleService scheduleService) {
        this.sessionService = sessionService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/mainpage")
    public Mono<String> mainpageAdmin(Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    User user = userService.getUserById(userId);
                    UserDto userDto = userMapper.toDto(user);
                    model.addAttribute("user", userDto);
                    return Mono.just("admin/admin_mainpage");
                });
    }

    @GetMapping("/schedule")
    public String schedule(Model model) {
        model.addAttribute("schedules", scheduleService.getAllSchedules());
        return "admin/admin_schedule";
    }

    @GetMapping("/user_management")
    public String userMenagment(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/user_menagment";
    }

    @GetMapping("/user_management/delete/{id}")
    public String deleteUser(@PathVariable int id) {
        userService.deleteUserById(id);
        return "redirect:/view/admin/user_management";
    }

    @GetMapping("/user_management/edit/{id}")
    public String editUserForm(@PathVariable int id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/user_menagment_edit";
    }

    @PostMapping("/user_management/edit/{id}/confirm")
    public String editUserFormConfirm(@PathVariable int id, @ModelAttribute User user) {
        User userOld = userService.getUserById(id);
        userService.updateUser(id, user);
        return "redirect:/view/admin/user_management";
    }
}



