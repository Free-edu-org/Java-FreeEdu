package com.byt.freeEdu.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import reactor.core.publisher.Mono;

import com.byt.freeEdu.controller.userSesion.SessionService;
import com.byt.freeEdu.mapper.GradeMapper;
import com.byt.freeEdu.mapper.ScheduleMapper;
import com.byt.freeEdu.mapper.UserMapper;
import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.model.DTO.GradeDto;
import com.byt.freeEdu.model.DTO.RemarkDto;
import com.byt.freeEdu.model.DTO.ScheduleDto;
import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.Grade;
import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.AttendanceService;
import com.byt.freeEdu.service.GradeService;
import com.byt.freeEdu.service.ScheduleService;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.UserService;

@Controller
@RequestMapping("/view/student")
public class ViewControllerStudent {

    private final ScheduleService scheduleService;

    private final UserService userService;

    private final UserMapper userMapper;

    private final AttendanceService attendanceService;

    private final GradeService gradeService;

    private final SessionService sessionService;

    private final StudentService studentService;

    private final ScheduleMapper scheduleMapper;

    private final GradeMapper gradeMapper;

    public ViewControllerStudent(ScheduleMapper scheduleMapper, ScheduleService scheduleService,
                                 UserService userService, UserMapper userMapper, AttendanceService attendanceService,
                                 GradeService gradeService, GradeMapper gradeMapper, SessionService sessionService,
                                 StudentService studentService) {
        this.scheduleService = scheduleService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.attendanceService = attendanceService;
        this.gradeService = gradeService;
        this.gradeMapper = gradeMapper;
        this.sessionService = sessionService;
        this.studentService = studentService;
        this.scheduleMapper = scheduleMapper;
    }

    @GetMapping("/mainpage")
    public Mono<String> mainpage(Model model) {
        return sessionService.getUserId().flatMap(userId -> {
            User user = userService.getUserById(userId);
            UserDto userDto = userMapper.toDto(user);
            model.addAttribute("user", userDto);
            return Mono.just("student/student_mainpage");
        });
    }

    @GetMapping("/remark")
    public Mono<String> getRemarks(Model model) {
        return sessionService.getUserId().flatMap(userId -> {
            List<RemarkDto> remarksDto = studentService.getRemarksForStudent(userId); // Użycie metody w
            // StudentService
            model.addAttribute("remarks", remarksDto);
            return Mono.just("student/student_remark");
        });
    }

    @GetMapping("/schedule")
    public Mono<String> getStudentSchedule(Model model) {
        return sessionService.getUserId().flatMap(userId -> {
            Student student = studentService.getStudentById(userId);
            List<Schedule> schedules = scheduleService.
                    getScheduleByClassId(student.getSchoolClass().getSchoolClassId());
            List<ScheduleDto> scheduleDtos = schedules.stream().map(scheduleMapper::toDto).
                    collect(Collectors.toList());
            model.addAttribute("schedules", scheduleDtos);
            return Mono.just("student/student_schedule");
        });
    }

    @GetMapping("/grade")
    public Mono<String> getStudentGrades(Model model) {
        return sessionService.getUserId().flatMap(userId -> {
            List<Grade> grades = gradeService.getGradesForStudent(userId);

            List<GradeDto> gradeDtos = grades.stream().map(gradeMapper::toDto).toList();

            model.addAttribute("grades", gradeDtos);
            return Mono.just("student/student_grade");
        });
    }

    @GetMapping("/attendance")
    public Mono<String> getAttendance(Model model) {
        return sessionService.getUserId().flatMap(userId -> {
            List<Attendance> attendances = attendanceService.getAttendancesForStudent(userId);
            model.addAttribute("attendances", attendances);
            return Mono.just("student/student_attendance");
        });
    }
}
