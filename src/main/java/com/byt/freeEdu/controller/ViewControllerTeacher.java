package com.byt.freeEdu.controller;

import com.byt.freeEdu.controller.userSesion.SessionService;
import com.byt.freeEdu.mapper.UserMapper;
import com.byt.freeEdu.model.DTO.ScheduleDto;
import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.*;
import com.byt.freeEdu.service.users.TeacherService;
import com.byt.freeEdu.service.users.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("/view/teacher")
public class ViewControllerTeacher {

    private final ScheduleService scheduleService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final AttendanceService attendanceService;
    private final GradeService gradeService;
    private final RemarkService remarkService;
    private final TeacherService teacherService;
    private final SessionService sessionService;

    public ViewControllerTeacher(ScheduleService scheduleService, UserService userService, UserMapper userMapper, AttendanceService attendanceService, GradeService gradeService, RemarkService remarkService, TeacherService teacherService, SessionService sessionService) {
        this.scheduleService = scheduleService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.attendanceService = attendanceService;
        this.gradeService = gradeService;
        this.remarkService = remarkService;
        this.teacherService = teacherService;
        this.sessionService = sessionService;
    }

    @GetMapping("/mainpage")
    public Mono<String> mainpageTeacher(Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    User user = userService.getUserById(userId);
                    UserDto userDto = userMapper.toDto(user); // Mapowanie User -> UserDto
                    model.addAttribute("user", userDto); // Przekazanie DTO do modelu
                    return Mono.just("teacher/teacher_mainpage");
                });
    }

    @GetMapping("/schedule")
    public Mono<String> scheduleTeacher(Model model) {
        return sessionService.getUserId()
                .map(userId -> {
                    List<ScheduleDto> scheduleDto = scheduleService.getSchedulesById(userId);
                    model.addAttribute("schedule", scheduleDto);

                    Teacher teacher = teacherService.getTeacherById(userId);
                    model.addAttribute("teacherId", teacher);

                    return "teacher/teacher_schedule";
                });
    }
}



