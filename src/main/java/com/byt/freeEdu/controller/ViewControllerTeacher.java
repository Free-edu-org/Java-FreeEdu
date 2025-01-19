package com.byt.freeEdu.controller;

import com.byt.freeEdu.controller.userSesion.SessionService;
import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.service.*;
import com.byt.freeEdu.service.users.TeacherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/view/teacher")
public class ViewControllerTeacher {

    private final ScheduleService scheduleService;
    private final AttendanceService attendanceService;
    private final GradeService gradeService;
    private final SchoolClassService schoolClassService;
    private final RemarkService remarkService;
    private final TeacherService teacherService;
    private final SessionService sessionService;

    public ViewControllerTeacher(ScheduleService scheduleService, AttendanceService attendanceService, GradeService gradeService, SchoolClassService schoolClassService, RemarkService remarkService, TeacherService teacherService, SessionService sessionService) {
        this.scheduleService = scheduleService;
        this.attendanceService = attendanceService;
        this.gradeService = gradeService;
        this.schoolClassService = schoolClassService;
        this.remarkService = remarkService;
        this.teacherService = teacherService;
        this.sessionService = sessionService;
    }

    @GetMapping("/mainpage")
    public Mono<String> mainpageTeacher() {
        return Mono.just("teacher/teacher_mainpage");
    }

    @GetMapping("/schedule")
    public Mono<String> scheduleTeacher(Model model) {
        return sessionService.getUserId()
                .map(userId -> {
                    Schedule schedule = scheduleService.getScheduleById(userId);
                    model.addAttribute("schedule", schedule);

                    Teacher teacher = teacherService.getTeacherById(userId);
                    model.addAttribute("teacherId", teacher);

                    return "teacher/teacher_schedule";
                });
    }
}



