package com.byt.freeEdu.controller;

import com.byt.freeEdu.controller.userSesion.SessionService;
import com.byt.freeEdu.mapper.GradeMapper;
import com.byt.freeEdu.mapper.RemarkMapper;
import com.byt.freeEdu.mapper.UserMapper;
import com.byt.freeEdu.model.DTO.*;
import com.byt.freeEdu.model.Grade;
import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.AttendanceService;
import com.byt.freeEdu.service.GradeService;
import com.byt.freeEdu.service.RemarkService;
import com.byt.freeEdu.service.ScheduleService;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;
import com.byt.freeEdu.service.users.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/view/teacher")
public class ViewControllerTeacher {

    private final ScheduleService scheduleService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final GradeMapper gradeMapper;
    private final GradeService gradeService;
    private final RemarkService remarkService;
    private final TeacherService teacherService;
    private final SessionService sessionService;
    private final StudentService studentService;

    public ViewControllerTeacher(ScheduleService scheduleService, UserService userService, UserMapper userMapper, RemarkMapper remarkMapper, AttendanceService attendanceService, GradeMapper gradeMapper, GradeService gradeService, RemarkService remarkService, TeacherService teacherService, SessionService sessionService, StudentService studentService) {
        this.scheduleService = scheduleService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.gradeMapper = gradeMapper;
        this.gradeService = gradeService;
        this.remarkService = remarkService;
        this.teacherService = teacherService;
        this.sessionService = sessionService;
        this.studentService = studentService;
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

    //Plan zajęć

    @GetMapping("/schedule")
    public Mono<String> getScheduleTeacher(Model model) {
        return sessionService.getUserId()
                .map(userId -> {
                    List<ScheduleDto> scheduleDto = scheduleService.getSchedulesById(userId);
                    model.addAttribute("schedule", scheduleDto);

                    Teacher teacher = teacherService.getTeacherById(userId);
                    model.addAttribute("teacherId", teacher);

                    return "teacher/teacher_schedule";
                });
    }

    //Uwagi

    @GetMapping("/remark")
    public Mono<String> getRemarksTeacher(Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    List<RemarkDto> remarksDto = remarkService.getTeacherRemarksById(userId);

                    model.addAttribute("remarks", remarksDto);
                    return Mono.just("teacher/teacher_remark");
                });
    }

    @GetMapping("/addRemark")
    public Mono<String> addRemarkForm(Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    List<StudentDto> students = studentService.getAllStudentsDto();
                    model.addAttribute("students", students);
                    model.addAttribute("remark", new RemarkDto());
                    return Mono.just("teacher/teacher_addRemark");
                });
    }

    @PostMapping("/addRemark")
    public Mono<String> addRemark(@ModelAttribute RemarkDto remarkDto) {
        return sessionService.getUserId()
                .flatMap(teacherId -> {
                    remarkService.addRemark(remarkDto.getContent(),remarkDto.getStudentId(), teacherId);
                    return Mono.just("redirect:/view/teacher/remark");
                });
    }

    @PostMapping("/deleteRemark/{id}")
    public String deleteRemark(@PathVariable("id") int remarkId, Model model) {
        try {
            remarkService.deleteRemark(remarkId);
            model.addAttribute("successMessage", "Uwagi zostały usunięte.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Nie udało się usunąć uwagi: " + e.getMessage());
        }
        return "redirect:/view/teacher/remark";
    }

    //Oceny

    @GetMapping("/grades")
    public Mono<String> getGrades(Model model) {
        return sessionService.getUserId()
                        .flatMap(userId ->{
                            model.addAttribute("grades", gradeService.getGradesByTeacherId(userId));
                            return Mono.just("teacher/teacher_grade");
                        });
    }

    @GetMapping("/addGrade")
    public Mono<String> showAddGradeForm(Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    model.addAttribute("grade", new GradeDto());
                    List<StudentDto> students = studentService.getAllStudentsDto();
                    model.addAttribute("students", students);
                    model.addAttribute("subjects", SubjectEnum.values());
                    model.addAttribute("teacherId", userId);
                    return Mono.just("teacher/teacher_addGrade");
                });
    }

    @PostMapping("/addGrade")
    public Mono<String> addGrade(@ModelAttribute GradeDto gradeDto, Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    try {
                        gradeDto.setTeacherId(userId);
                        gradeDto.setGradeDate(LocalDate.now());
                        boolean success = gradeService.saveGrade(gradeDto);

                        if (!success) {
                            model.addAttribute("error", "Nie udało się dodać oceny. Spróbuj ponownie.");
                            return Mono.just("teacher/add_grade");
                        }

                        return Mono.just("redirect:/view/teacher/grades");
                    } catch (IllegalArgumentException ex) {
                        model.addAttribute("errorMessage", ex.getMessage());
                        return Mono.just("teacher/add_grade");
                    }
                });
    }

    @PostMapping("/deleteGrade/{gradeId}")
    public Mono<String> deleteGrade(@PathVariable int gradeId) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    gradeService.deleteGrade(gradeId);
                    return Mono.just("redirect:/view/teacher/grades");
                });
    }

    @GetMapping("/editGrade/{gradeId}")
    public Mono<String> showEditGradeForm(@PathVariable int gradeId, Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    GradeDto dto = gradeMapper.toDto(gradeService.getGradeById(gradeId));
                    if (dto == null) {
                        model.addAttribute("errorMessage", "Nie znaleziono oceny o podanym ID.");
                        return Mono.just("teacher/teacher_grade");
                    }
                    model.addAttribute("grade", dto);
                    return Mono.just("teacher/teacher_editGrade");
                });
    }

    @PostMapping("/editGrade/{gradeId}")
    public Mono<String> editGrade(@PathVariable int gradeId, @ModelAttribute GradeDto gradeDto, Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    try {
                        boolean success = gradeService.updateGrade(gradeDto.getGradeId(), gradeDto);
                        if (!success) {
                            model.addAttribute("errorMessage", "Nie udało się zapisać zmian. Spróbuj ponownie.");
                            return Mono.just("teacher/teacher_editGrade");
                        }
                        return Mono.just("redirect:/view/teacher/grades");
                    } catch (Exception ex) {
                        model.addAttribute("errorMessage", "Wystąpił błąd: " + ex.getMessage());
                        return Mono.just("teacher/teacher_editGrade");
                    }
                });
    }
}



