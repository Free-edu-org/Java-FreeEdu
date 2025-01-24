package com.byt.freeEdu.controller;

import com.byt.freeEdu.controller.userSesion.SessionService;
import com.byt.freeEdu.mapper.UserMapper;
import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.users.UserService;
import com.byt.freeEdu.service.users.ParentService; // Dodany import
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("/view/parent")
public class ViewControllerParent {

    private final UserService userService;
    private final UserMapper userMapper;
    private final SessionService sessionService;
    private final ParentService parentService; // Dodane pole

    // Konstruktor z wymaganymi zależnościami
    public ViewControllerParent(UserService userService, UserMapper userMapper, SessionService sessionService, ParentService parentService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.sessionService = sessionService;
        this.parentService = parentService; // Inicjalizacja ParentService
    }

    @GetMapping("/mainpage")
    public Mono<String> mainpageParent(Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    User user = userService.getUserById(userId);
                    UserDto userDto = userMapper.toDto(user);
                    model.addAttribute("user", userDto);

                    // Pobierz uczniów powiązanych z rodzicem
                    List<Student> students = parentService.getStudentsByParentId(userId); // Użycie parentService
                    model.addAttribute("students", students);

                    return Mono.just("parent/parent_mainpage");
                });
    }
}