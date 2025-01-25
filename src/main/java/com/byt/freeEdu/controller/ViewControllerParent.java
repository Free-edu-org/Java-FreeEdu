package com.byt.freeEdu.controller;

import com.byt.freeEdu.controller.userSesion.SessionService;
import com.byt.freeEdu.mapper.*;
import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.model.DTO.*;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.*;
import com.byt.freeEdu.service.users.ParentService;
import com.byt.freeEdu.service.users.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/view/parent")
public class ViewControllerParent {

    private final ScheduleService scheduleService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final SessionService sessionService;
    private final ParentService parentService;
    private final ScheduleMapper scheduleMapper;
    private final GradeService gradeService;
    private final GradeMapper gradeMapper;
    private final AttendanceService attendanceService;
    private final AttendanceMapper attendanceMapper;
    private final RemarkService remarkService;
    private final RemarkMapper remarkMapper;

    public ViewControllerParent(UserService userService, UserMapper userMapper, SessionService sessionService,
                                ParentService parentService, ScheduleService scheduleService, ScheduleMapper scheduleMapper,
                                GradeService gradeService, GradeMapper gradeMapper, AttendanceService attendanceService,
                                AttendanceMapper attendanceMapper, RemarkService remarkService, RemarkMapper remarkMapper) {
        this.scheduleService = scheduleService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.sessionService = sessionService;
        this.parentService = parentService;
        this.scheduleMapper = scheduleMapper;
        this.gradeService = gradeService;
        this.gradeMapper = gradeMapper;
        this.attendanceService = attendanceService;
        this.attendanceMapper = attendanceMapper;
        this.remarkService = remarkService;
        this.remarkMapper = remarkMapper;
    }

    @GetMapping("/mainpage")
    public Mono<String> mainpageParent(Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    User user = userService.getUserById(userId);
                    UserDto userDto = userMapper.toDto(user);
                    model.addAttribute("user", userDto);

                    // Pobierz uczniów powiązanych z rodzicem
                    List<Student> students = parentService.getStudentsByParentId(userId);
                    model.addAttribute("students", students);

                    return Mono.just("parent/parent_mainpage");
                });
    }

    @GetMapping("/schedule")
    public Mono<String> getStudentSchedule(@RequestParam int studentId, Model model) {
        return sessionService.getUserId()
                .flatMap(parentId -> {
                    // Pobierz listę uczniów powiązanych z rodzicem
                    List<Student> students = parentService.getStudentsByParentId(parentId);

                    // Znajdź ucznia o podanym studentId
                    Student student = students.stream()
                            .filter(s -> s.getUserId() == studentId)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Uczeń nie znaleziony"));

                    // Pobierz plan lekcji dla klasy ucznia
                    List<ScheduleDto> schedules = scheduleService.getScheduleByClassId(student.getSchoolClass().getSchoolClassId())
                            .stream()
                            .map(scheduleMapper::toDto)
                            .collect(Collectors.toList());

                    // Dodaj dane do modelu
                    model.addAttribute("schedules", schedules);
                    model.addAttribute("studentName", student.getFirstname() + " " + student.getLastname());

                    return Mono.just("parent/parent_schedule");
                });
    }

    @GetMapping("/grade")
    public Mono<String> getStudentGrades(@RequestParam int studentId, Model model) {
        return sessionService.getUserId()
                .flatMap(parentId -> {
                    // Pobierz listę uczniów powiązanych z rodzicem
                    List<Student> students = parentService.getStudentsByParentId(parentId);

                    // Znajdź ucznia o podanym studentId
                    Student student = students.stream()
                            .filter(s -> s.getUserId() == studentId)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Uczeń nie znaleziony"));

                    // Pobierz oceny ucznia
                    List<GradeDto> grades = gradeService.getGradesForStudent(studentId)
                            .stream()
                            .map(gradeMapper::toDto)
                            .collect(Collectors.toList());

                    // Dodaj dane do modelu
                    model.addAttribute("grades", grades);
                    model.addAttribute("studentName", student.getFirstname() + " " + student.getLastname());

                    return Mono.just("parent/parent_grade");
                });
    }

    @GetMapping("/attendance")
    public Mono<String> getStudentAttendance(@RequestParam int studentId, Model model) {
        return sessionService.getUserId()
                .flatMap(parentId -> {
                    // Pobierz listę uczniów powiązanych z rodzicem
                    List<Student> students = parentService.getStudentsByParentId(parentId);

                    // Znajdź ucznia o podanym studentId
                    Student student = students.stream()
                            .filter(s -> s.getUserId() == studentId)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Uczeń nie znaleziony"));

                    // Pobierz frekwencję ucznia
                    List<Attendance> attendances = attendanceService.getAttendancesForStudent(studentId);
                    List<AttendanceDto> attendanceDtos = attendances.stream()
                            .map(attendanceMapper::toAttendanceDto) // Użyj toAttendanceDto zamiast toDto
                            .collect(Collectors.toList());

                    // Dodaj dane do modelu
                    model.addAttribute("attendances", attendanceDtos);
                    model.addAttribute("studentName", student.getFirstname() + " " + student.getLastname());

                    return Mono.just("parent/parent_attendance");
                });
    }

    @GetMapping("/remark/{id}")
    public Mono<String> getStudentRemarks(@PathVariable int id, Model model) {
        return sessionService.getUserId()
                .flatMap(parentId -> {
                    // Pobierz listę uczniów powiązanych z rodzicem
                    List<Student> students = parentService.getStudentsByParentId(parentId);

                    // Znajdź ucznia o podanym studentId
                    Student student = students.stream()
                            .filter(s -> s.getUserId() == id)
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Uczeń nie znaleziony"));

                    // Pobierz uwagi ucznia
                    var remarks = remarkService.getRemarksByStudentId(id);

                    // Dodaj dane do modelu
                    model.addAttribute("remarks", remarks);
                    model.addAttribute("studentName", student.getFirstname() + " " + student.getLastname());

                    return Mono.just("parent/parent_remark");
                });
    }
}