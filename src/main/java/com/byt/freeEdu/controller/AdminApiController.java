package com.byt.freeEdu.controller;

import com.byt.freeEdu.mapper.AttendanceMapper;
import com.byt.freeEdu.mapper.GradeMapper;
import com.byt.freeEdu.mapper.ScheduleMapper;
import com.byt.freeEdu.mapper.UserMapper;
import com.byt.freeEdu.model.DTO.*;
import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.service.*;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;
import com.byt.freeEdu.service.users.UserService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final RemarkService remarkService;

    private final GradeService gradeService;

    private final GradeMapper gradeMapper;

    private final ScheduleService scheduleService;

    private final ScheduleMapper scheduleMapper;

    private final AttendanceService attendanceService;

    private final AttendanceMapper attendanceMapper;

    private final SchoolClassService schoolClassService;

    private final UserService userService;

    private final UserMapper userMapper;

    private final TeacherService teacherService;

    private final StudentService studentService;

    public AdminApiController(
            RemarkService remarkService,
            GradeService gradeService,
            GradeMapper gradeMapper,
            ScheduleService scheduleService,
            ScheduleMapper scheduleMapper,
            AttendanceService attendanceService,
            AttendanceMapper attendanceMapper,
            SchoolClassService schoolClassService,
            UserService userService,
            UserMapper userMapper,
            TeacherService teacherService,
            StudentService studentService) {
        this.remarkService = remarkService;
        this.gradeService = gradeService;
        this.gradeMapper = gradeMapper;
        this.scheduleService = scheduleService;
        this.scheduleMapper = scheduleMapper;
        this.attendanceService = attendanceService;
        this.attendanceMapper = attendanceMapper;
        this.schoolClassService = schoolClassService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    /* ===== Słowniki ===== */

    @GetMapping("/subjects")
    public Flux<Map<String, String>> subjects() {
        return Flux.fromIterable(
                Arrays.stream(SubjectEnum.values())
                        .map(s -> Map.of("name", s.name(),
                                "displayName", s.getDisplayName() != null ? s.getDisplayName() : s.name()))
                        .collect(Collectors.toList()));
    }

    @GetMapping("/teachers")
    public Flux<Map<String, Object>> teachers() {
        return Flux.fromIterable(
                teacherService.getAllTeachers().stream()
                        .map(t -> Map.<String, Object>of(
                                "userId", t.getUserId(),
                                "firstname", t.getFirstname(),
                                "lastname", t.getLastname()))
                        .collect(Collectors.toList()));
    }

    @GetMapping("/students")
    public Flux<Map<String, Object>> students() {
        return Flux.fromIterable(
                studentService.getAllStudents().stream()
                        .map(s -> Map.<String, Object>of(
                                "userId", s.getUserId(),
                                "firstname", s.getFirstname(),
                                "lastname", s.getLastname()))
                        .collect(Collectors.toList()));
    }

    /* ===== Uwagi ===== */

    @GetMapping("/remarks")
    public Flux<Map<String, Object>> listRemarks() {
        return Flux.fromIterable(
                remarkService.getAllRemarks().stream()
                        .map(r -> Map.<String, Object>of(
                                "id", r.getRemarkId(),
                                "studentFirstName", r.getStudentFirstName(),
                                "studentLastName", r.getStudentLastName(),
                                "studentId", r.getStudentId(),
                                "teacherFirstName", r.getTeacherFirstName(),
                                "teacherLastName", r.getTeacherLastName(),
                                "teacherId", r.getTeacherId(),
                                "content", r.getContent(),
                                "addDate", r.getAddDate()))
                        .collect(Collectors.toList()));
    }

    @PostMapping("/remarks")
    public Mono<Void> addRemark(@RequestBody Map<String, Object> req) {
        String content = (String) req.get("content");
        Integer studentId = req.get("studentId") != null ? ((Number) req.get("studentId")).intValue() : null;
        Integer teacherId = req.get("teacherId") != null ? ((Number) req.get("teacherId")).intValue() : null;
        remarkService.addRemark(content, studentId, teacherId);
        return Mono.empty();
    }

    @PutMapping("/remarks/{id}")
    public Mono<Void> updateRemark(@PathVariable int id, @RequestBody RemarkDto dto) {
        remarkService.updateRemark(id, dto);
        return Mono.empty();
    }

    @DeleteMapping("/remarks/{id}")
    public Mono<Void> deleteRemark(@PathVariable int id) {
        remarkService.deleteRemark(id);
        return Mono.empty();
    }

    /* ===== Oceny ===== */

    @GetMapping("/grades")
    public Flux<Map<String, Object>> grades() {
        return Flux.fromIterable(
                gradeService.getAllGrades().stream()
                        .map(dto -> {
                            Map<String, Object> map = new java.util.HashMap<>();
                            map.put("gradeId", dto.getGradeId());
                            map.put("studentFirstName", dto.getStudentFirstName());
                            map.put("studentLastName", dto.getStudentLastName());
                            map.put("studentId", dto.getStudentId());
                            map.put("subject", dto.getSubject());
                            map.put("subjectCode", dto.getSubjectEnum() != null ? dto.getSubjectEnum().name() : null);
                            map.put("value", dto.getValue());
                            map.put("gradeDate", dto.getGradeDate() != null ? dto.getGradeDate().toString() : null);
                            map.put("teacherId", dto.getTeacherId());
                            map.put("teacherFirstName", dto.getTeacherFirstName());
                            map.put("teacherLastName", dto.getTeacherLastName());
                            return map;
                        })
                        .collect(Collectors.toList()));
    }

    @PostMapping("/grades")
    public Mono<Void> addGrade(@RequestBody GradeDto dto) {
        if (dto.getGradeDate() == null) {
            dto.setGradeDate(LocalDate.now());
        }
        gradeService.saveGrade(dto);
        return Mono.empty();
    }

    @PutMapping("/grades/{id}")
    public Mono<Void> updateGrade(@PathVariable int id, @RequestBody GradeDto dto) {
        gradeService.updateGrade(id, dto);
        return Mono.empty();
    }

    @DeleteMapping("/grades/{id}")
    public Mono<Void> deleteGrade(@PathVariable int id) {
        gradeService.deleteGrade(id);
        return Mono.empty();
    }

    /* ===== Plan ===== */

    @GetMapping("/schedule")
    public Flux<ScheduleAdminDto> scheduleList() {
        return Flux.fromIterable(scheduleService.getAllSchedules());
    }

    @PostMapping("/schedule")
    public Mono<Void> scheduleAdd(@RequestBody ScheduleDto dto) {
        Schedule schedule = new Schedule();
        schedule.setDate(dto.getDate());
        schedule.setSubject(SubjectEnum.valueOf(dto.getSubjectName()));
        schedule.setSchoolClass(schoolClassService.getSchoolClassById(Integer.parseInt(dto.getClassName())));
        schedule.setTeacher(teacherService.getTeacherById(dto.getTeacherId()));
        scheduleService.addSchedule(schedule);
        return Mono.empty();
    }

    @PutMapping("/schedule/{id}")
    public Mono<Void> scheduleUpdate(@PathVariable int id, @RequestBody ScheduleDto dto) {
        scheduleService.updateSchedule(id, dto);
        return Mono.empty();
    }

    @DeleteMapping("/schedule/{id}")
    public Mono<Void> scheduleDelete(@PathVariable int id) {
        scheduleService.deleteSchedule(id);
        return Mono.empty();
    }

    /* ===== Frekwencja ===== */

    @GetMapping("/attendance")
    public Flux<AttendanceDto> attendanceList() {
        return Flux.fromIterable(attendanceService.getAllAttendancesAdmin());
    }

    @GetMapping("/attendance/{id}")
    public Mono<AttendanceDto> attendanceGet(@PathVariable int id) {
        return Mono.just(attendanceService.getAttendanceByIdAdmin(id));
    }

    @PostMapping("/attendance")
    public Mono<Void> attendanceAdd(@RequestBody AttendanceDto dto) {
        attendanceService.saveAttendance(dto.toEntity(
                studentService.getStudentById(dto.getStudentId()),
                teacherService.getTeacherById(dto.getTeacherId())));
        return Mono.empty();
    }

    @PutMapping("/attendance/{id}")
    public Mono<Void> attendanceUpdate(@PathVariable int id, @RequestBody AttendanceDto dto) {
        attendanceService.updateAttendanceAdmin(id, dto);
        return Mono.empty();
    }

    @DeleteMapping("/attendance/{id}")
    public Mono<Void> attendanceDelete(@PathVariable int id) {
        attendanceService.deleteAttendance(id);
        return Mono.empty();
    }

    /* ===== Klasy ===== */

    @GetMapping("/classes")
    public Flux<SchoolClass> classesList() {
        return Flux.fromIterable(schoolClassService.getAllClassesWithStudentCount());
    }

    @PostMapping("/classes")
    public Mono<Void> classAdd(@RequestBody String name) {
        schoolClassService.addSchoolClass(name);
        return Mono.empty();
    }

    @PutMapping("/classes/{id}")
    public Mono<Void> classUpdate(@PathVariable int id, @RequestBody String name) {
        schoolClassService.updateSchoolClass(id, name);
        return Mono.empty();
    }

    @DeleteMapping("/classes/{id}")
    public Mono<Void> classDelete(@PathVariable int id) {
        schoolClassService.deleteSchoolClassById(id);
        return Mono.empty();
    }

    /* ===== Użytkownicy ===== */

    @GetMapping("/users")
    public Flux<UserDto> usersList() {
        return Flux.fromIterable(
                userService.getAllUsers().stream()
                        .map(userMapper::toDto)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/users/{id}")
    public Mono<UserDto> getUserById(@PathVariable int id) {
        return Mono.just(userMapper.toDto(userService.getUserById(id)));
    }

    @PostMapping("/users")
    public Mono<Void> userAdd(@RequestBody UserDto user) {
        userService.addUser(user);
        return Mono.empty();
    }

    @PutMapping("/users/{id}")
    public Mono<Void> userUpdate(@PathVariable int id, @RequestBody UserDto user) {
        userService.updateUser(id, user);
        return Mono.empty();
    }

    @DeleteMapping("/users/{id}")
    public Mono<Void> userDelete(@PathVariable int id) {
        userService.deleteUserById(id);
        return Mono.empty();
    }
}
