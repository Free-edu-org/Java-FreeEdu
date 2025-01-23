package com.byt.freeEdu.controller;

import com.byt.freeEdu.controller.userSesion.SessionService;
import com.byt.freeEdu.mapper.*;
import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.model.DTO.*;
import com.byt.freeEdu.model.Grade;
import com.byt.freeEdu.model.Remark;
import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.AttendanceService;
import com.byt.freeEdu.service.GradeService;
import com.byt.freeEdu.service.RemarkService;
import com.byt.freeEdu.service.ScheduleService;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;
import com.byt.freeEdu.service.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/view/student")
public class ViewControllerStudent {
    private final ScheduleService scheduleService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final RemarkMapper remarkMapper;
    private final AttendanceService attendanceService;
    private final GradeService gradeService;
    private final RemarkService remarkService;
    private final SessionService sessionService;
    private final StudentService studentService;
    private final ScheduleMapper scheduleMapper;
    private final GradeMapper gradeMapper;
    private final AttendanceMapper attendanceMapper;


    public ViewControllerStudent(ScheduleMapper scheduleMapper, ScheduleService scheduleService, UserService userService, UserMapper userMapper, RemarkMapper remarkMapper, AttendanceService attendanceService, AttendanceMapper attendanceMapper, GradeService gradeService, GradeMapper gradeMapper, RemarkService remarkService, TeacherService teacherService, SessionService sessionService, StudentService studentService) {
        this.scheduleService = scheduleService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.remarkMapper = remarkMapper;
        this.attendanceService = attendanceService;
        this.gradeService = gradeService;
        this.remarkService = remarkService;
        this.gradeMapper = gradeMapper;
        this.sessionService = sessionService;
        this.studentService = studentService;

        this.attendanceMapper = attendanceMapper;
        this.scheduleMapper = scheduleMapper;
    }

    @GetMapping("/mainpage")
    public Mono<String> mainpage(Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    User user = userService.getUserById(userId);
                    UserDto userDto = userMapper.toDto(user);
                    model.addAttribute("user", userDto);
                    return Mono.just("student/student_mainpage");
                });
    }


    @GetMapping("/remark")
    public Mono<String> getRemarks(Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    List<RemarkDto> remarksDto = studentService.getRemarksForStudent(userId); // Użycie metody w StudentService
                    model.addAttribute("remarks", remarksDto);
                    return Mono.just("student/student_remark");
                });
    }

    @GetMapping("/schedule")
    public Mono<String> getStudentSchedule(Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    Student student = studentService.getStudentById(userId);
                    List<Schedule> schedules = scheduleService.getScheduleByClassId(student.getSchoolClass().getSchoolClassId());
                    List<ScheduleDto> scheduleDtos = schedules.stream()
                            .map(scheduleMapper::toDto)
                            .collect(Collectors.toList());
                    model.addAttribute("schedules", scheduleDtos);
                    return Mono.just("student/student_schedule");
                });
    }

    @GetMapping("/grade")
    public Mono<String> getStudentGrades(Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    // Pobierz oceny dla użytkownika
                    List<Grade> grades = gradeService.getGradesForStudent(userId);

                    // Zamapuj encje na DTO za pomocą mappera
                    List<GradeDto> gradeDtos = grades.stream()
                            .map(gradeMapper::toDto)
                            .toList();

                    // Dodaj dane do modelu
                    model.addAttribute("grades", gradeDtos);
                    return Mono.just("student/student_grade");
                });
    }

    @GetMapping("/attendance")
    public Mono<String> getAttendance(Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    List<Attendance> attendances = attendanceService.getAttendancesForStudent(userId);
                    model.addAttribute("attendances", attendances);
                    return Mono.just("student/student_attendance");
                });
    }





}

