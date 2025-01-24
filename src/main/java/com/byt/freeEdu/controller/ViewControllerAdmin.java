package com.byt.freeEdu.controller;

import com.byt.freeEdu.controller.userSesion.SessionService;
import com.byt.freeEdu.mapper.UserMapper;
import com.byt.freeEdu.model.DTO.*;
import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.*;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;
import com.byt.freeEdu.service.users.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("/view/admin")
public class ViewControllerAdmin {
    private final SessionService sessionService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ScheduleService scheduleService;
    private final GradeService gradeService;
    private final SchoolClassService schoolClassService;
    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final RemarkService remarkService;
    private final TeacherService teacherService;

    public ViewControllerAdmin(
            SessionService sessionService,
            UserService userService,
            UserMapper userMapper,
            ScheduleService scheduleService,
            GradeService gradeService,
            SchoolClassService schoolClassService,
            StudentService studentService,
            AttendanceService attendanceService,
            RemarkService remarkService,
            TeacherService teacherService
    ) {
        this.sessionService = sessionService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.scheduleService = scheduleService;
        this.gradeService = gradeService;
        this.schoolClassService = schoolClassService;
        this.studentService = studentService;
        this.attendanceService = attendanceService;
        this.remarkService = remarkService;
        this.teacherService = teacherService;
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
        return "admin/schedule";
    }

    @GetMapping("/schedule/add")
    public String addScheduleForm() {
        return "admin/schedule_add";
    }

    @GetMapping("/schedule/add/confirm")
    public String addScheduleFormConfirm(Model model, @ModelAttribute ScheduleDto scheduleDto) {
        Schedule schedule = new Schedule(
                scheduleDto.getId(),
                scheduleDto.getDate(),
                SubjectEnum.valueOf(scheduleDto.getSubjectName()),
                new SchoolClass(),
                new Teacher()
        );

        return "admin/schedule_add";
    }

    @GetMapping("/schedule/delete/{id}")
    public String deleteSchedule(@PathVariable int id) {
        scheduleService.deleteSchedule(id);
        return "redirect:/view/admin/schedule";
    }

    @GetMapping("/schedule/edit/{id}")
    public String editScheduleForm(@PathVariable int id, Model model) {
        List<ScheduleDto> schedules = scheduleService.getSchedulesById(id);
        model.addAttribute("schedule", schedules.get(0));

        List<Teacher> teachers = teacherService.getAllTeachers();
        model.addAttribute("teachers", teachers);

        List<SchoolClass> schoolClasses = schoolClassService.getAllSchoolClass();
        model.addAttribute("schoolClasses", schoolClasses);

        return "admin/schedule_edit";
    }

    @PostMapping("/schedule/edit/{id}/confirm")
    public String editScheduleFormConfirm(@PathVariable int id, @ModelAttribute Schedule schedule) {

        scheduleService.updateSchedule(id, schedule);
        return "redirect:/view/admin/schedule";
    }

    @GetMapping("/grade")
    public String getGrades(Model model) {
        model.addAttribute("grades", gradeService.getAllGrades());
        return "admin/grade";
    }

    @GetMapping("/attendance")
    public String attendanceSelect(Model model) {
        List<SchoolClass> schoolClasses = schoolClassService.getAllClassesWithStudentCount();
        model.addAttribute("schoolClasses", schoolClasses);
        return "admin/attendanceSelect";

    }

    @GetMapping("/attendance/{classId}")
    public String markAttendance(@PathVariable int classId, Model model) {
        SchoolClass schoolClass = schoolClassService.getSchoolClassById(classId);
        if (schoolClass == null) {
            model.addAttribute("errorMessage", "Nie znaleziono klasy o podanym ID.");
            return "admin/attendanceMark";
        }

        List<StudentDto> students = studentService.getStudentsBySchoolClassId(classId);
        if (students.isEmpty()) {
            model.addAttribute("errorMessage", "Brak uczniów w wybranej klasie.");
            return "admin/attendanceMark";
        }

        model.addAttribute("schoolClass", schoolClass);
        model.addAttribute("students", students);
        model.addAttribute("attendanceForm", new AttendanceFormDto()); // Dodajemy DTO
        return "admin/attendanceMark";
    }

    @PostMapping("/attendance/mark")
    public Mono<String> saveAttendance(@ModelAttribute AttendanceFormDto attendanceFormDto, Model model) {
        return sessionService.getUserId()
                .flatMap(userId -> {
                    if (attendanceFormDto.getAttendanceMap().isEmpty()) {
                        model.addAttribute("errorMessage", "Nie zaznaczono żadnej obecności.");
                        return Mono.just("/view/admin/attendanceMark");
                    }

                    attendanceService.markAttendance(
                            attendanceFormDto.getAttendanceMap(),
                            attendanceFormDto.getGlobalSubject(),
                            userId
                    );
                    return Mono.just("redirect:/view/admin/attendance");
                });
    }

    @GetMapping("/remark")
    public Mono<String> getRemarksTeacher(Model model) {
        model.addAttribute("remarks",  remarkService.getAllRemarks());

        return Mono.just("admin/remark");
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
        userService.updateUser(id, user);
        return "redirect:/view/admin/user_management";
    }
}



