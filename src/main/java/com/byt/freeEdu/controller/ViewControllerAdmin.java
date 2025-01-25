package com.byt.freeEdu.controller;

import com.byt.freeEdu.controller.userSesion.SessionService;
import com.byt.freeEdu.mapper.GradeMapper;
import com.byt.freeEdu.mapper.UserMapper;
import com.byt.freeEdu.model.DTO.*;
import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.*;
import com.byt.freeEdu.service.users.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
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
    private final GradeMapper gradeMapper;
    private final ParentService parentService;
    private final AdminService adminService;

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
            TeacherService teacherService,
            GradeMapper gradeMapper,
            ParentService parentService,
            AdminService adminService
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
        this.gradeMapper = gradeMapper;
        this.parentService = parentService;
        this.adminService = adminService;
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
    public String addScheduleForm(Model model) {
        List<Teacher> teachers = teacherService.getAllTeachers();
        model.addAttribute("teachers", teachers);

        List<SchoolClass> schoolClasses = schoolClassService.getAllSchoolClass();
        model.addAttribute("schoolClasses", schoolClasses);

        model.addAttribute("subjects", SubjectEnum.values());

        model.addAttribute("schedule", new ScheduleDto());
        return "admin/schedule_add";
    }

    @PostMapping("/schedule/add/confirm")
    public String addScheduleFormConfirm(@ModelAttribute ScheduleDto scheduleDto) {
        Schedule schedule = new Schedule();
        schedule.setDate(scheduleDto.getDate());
        schedule.setSubject(SubjectEnum.valueOf(scheduleDto.getSubjectName()));
        schedule.setSchoolClass(schoolClassService.getSchoolClassById(Integer.parseInt(scheduleDto.getClassName())));
        schedule.setTeacher(teacherService.getTeacherById(scheduleDto.getTeacherId()));

        scheduleService.addSchedule(schedule);
        return "redirect:/view/admin/schedule";
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
    public String editScheduleFormConfirm(@PathVariable int id, @ModelAttribute ScheduleDto scheduleDto) {
        Schedule schedule = new Schedule();
        schedule.setScheduleId(id);
        schedule.setDate(scheduleDto.getDate());
        schedule.setSubject(SubjectEnum.valueOf(scheduleDto.getSubjectName()));
        schedule.setSchoolClass(schoolClassService.getSchoolClassById(Integer.parseInt(scheduleDto.getClassName())));
        schedule.setTeacher(teacherService.getTeacherById(scheduleDto.getTeacherId()));

        scheduleService.updateSchedule(id, schedule);
        return "redirect:/view/admin/schedule";
    }

    @GetMapping("/grade")
    public String getGrades(Model model) {
        model.addAttribute("grades", gradeService.getAllGrades());
        return "admin/grade";
    }

    @GetMapping("/grade/add")
    public String addGradeForm(Model model) {
        model.addAttribute("grade", new GradeDto());
        model.addAttribute("subjects", SubjectEnum.values());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("students", studentService.getAllStudents());
        return "admin/grade_add";
    }

    @PostMapping("/grade/add/confirm")
    public String addGradeFormConfirm(@ModelAttribute GradeDto gradeDto) {
        gradeDto.setGradeDate(LocalDate.now());
        gradeService.saveGrade(gradeDto);
        return "redirect:/view/admin/grade";
    }

    @GetMapping("/grade/edit/{id}")
    public String editGradeForm(@PathVariable int id, Model model) {
        GradeDto grade = gradeMapper.toDto(gradeService.getGradeById(id));
        model.addAttribute("grade", grade);
        model.addAttribute("subjects", SubjectEnum.values());
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("students", studentService.getAllStudents());
        return "admin/grade_edit";
    }

    @PostMapping("/grade/edit/{id}/confirm")
    public String editGradeFormConfirm(@PathVariable int id, @ModelAttribute GradeDto gradeDto) {
        gradeService.updateGrade(id, gradeDto);
        return "redirect:/view/admin/grade";
    }

    @PostMapping("/grade/delete/{id}")
    public String deleteGrade(@PathVariable int id) {
        gradeService.deleteGrade(id);
        return "redirect:/view/admin/grade";
    }

    @GetMapping("/schoolClass")
    public String getSchoolClasses(Model model) {
        List<SchoolClass> schoolClasses = schoolClassService.getAllSchoolClass();
        for (SchoolClass schoolClass : schoolClasses) {
            List<StudentDto> students = studentService.getStudentsBySchoolClassId(schoolClass.getSchoolClassId());
            schoolClass.setStudents(students);
        }
        model.addAttribute("schoolClasses", schoolClasses);
        model.addAttribute("newClass", new SchoolClass());
        return "admin/schoolClass";
    }

    @PostMapping("/schoolClass/add")
    public String addSchoolClass(@ModelAttribute SchoolClass schoolClass) {
        schoolClassService.addSchoolClass(schoolClass.getName());
        return "redirect:/view/admin/schoolClass";
    }

    @PostMapping("/schoolClass/delete/{id}")
    public String deleteSchoolClass(@PathVariable int id) {
        schoolClassService.deleteSchoolClassById(id);
        return "redirect:/view/admin/schoolClass";
    }

    @GetMapping("/schoolClass/changeStudentClassForm/{studentId}")
    public String changeStudentClassForm(Model model, @PathVariable("studentId") int studentId) {
        model.addAttribute("student", studentService.getStudentById(studentId));
        model.addAttribute("schoolClasses", schoolClassService.getAllSchoolClass());
        return "admin/schoolClass_changeStudentClassForm";
    }

    @PostMapping("/schoolClass/changeStudentClassForm/{studentId}/{schoolClassId}")
    public String changeStudentClassFormConfirm(@PathVariable("studentId") int studentId, @PathVariable("schoolClassId") int schoolClassId) {
        studentService.changeStudentClass(studentId, schoolClassService.getSchoolClassById(schoolClassId));
        return "redirect:/view/admin/schoolClass";
    }

    @GetMapping("/attendance")
    public String getAttendance(Model model) {
        List<AttendanceDto> attendances = attendanceService.getAllAttendancesAdmin();

        model.addAttribute("attendances", attendances);

        return "admin/attendance";
    }

    @GetMapping("/attendance/edit/{id}")
    public String editAttendanceForm(@PathVariable("id") int attendanceId, Model model) {
        AttendanceDto attendance = attendanceService.getAttendanceByIdAdmin(attendanceId);
        if (attendance == null) {
            model.addAttribute("errorMessage", "Nie znaleziono obecności o podanym ID.");
            return "admin/attendance";
        }

        model.addAttribute("attendance", attendance);

        return "admin/attendance_edit";
    }

    @PostMapping("/attendance/edit/{id}")
    public String editAttendance(@PathVariable("id") int attendanceId, @ModelAttribute AttendanceDto attendanceDto) {
        attendanceService.updateAttendanceAdmin(attendanceId, attendanceDto);

        return "redirect:/view/admin/attendance";
    }

    @PostMapping("/attendance/delete/{id}")
    public String deleteAttendance(@PathVariable("id") int attendanceId, Model model) {
        try {
            attendanceService.deleteAttendance(attendanceId);
            model.addAttribute("successMessage", "Obecność została usunięta.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Nie udało się usunąć obecności: " + e.getMessage());
        }
        return "redirect:/view/admin/attendance";
    }

    @GetMapping("/remark")
    public Mono<String> getRemarksTeacher(Model model) {
        model.addAttribute("remarks", remarkService.getAllRemarks());

        return Mono.just("admin/remark");
    }

    @PostMapping("/deleteRemark/{id}")
    public String deleteRemark(@PathVariable("id") int remarkId, Model model) {
        try {
            remarkService.deleteRemark(remarkId);
            model.addAttribute("successMessage", "Uwagi zostały usunięte.");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Nie udało się usunąć uwagi: " + e.getMessage());
        }
        return "redirect:/view/admin/remark";
    }

    @GetMapping("/editRemark/{remarkId}")
    public String editRemarkForm(@PathVariable int remarkId, Model model) {
        RemarkDto remarkDto = remarkService.getAdminRemarkById(remarkId);
        if (remarkDto == null) {
            model.addAttribute("errorMessage", "Nie znaleziono uwagi o podanym ID.");
            return "admin/remark";
        }

        model.addAttribute("remark", remarkDto);

        return "admin/remark_edit";
    }

    @PostMapping("/editRemark/{remarkId}")
    public String editRemark(@PathVariable int remarkId, @ModelAttribute RemarkDto remarkDto) {
        remarkService.updateRemark(remarkId, remarkDto);

        return "redirect:/view/admin/remark";
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
        UserDto user = userMapper.toDto(userService.getUserById(id));
        model.addAttribute("user", user);
        model.addAttribute("schoolClasses", schoolClassService.getAllSchoolClass());
        model.addAttribute("parents", parentService.getAllParents());

        return "admin/user_menagment_edit";
    }

    @PostMapping("/user_management/edit/{id}/confirm")
    public String editUserFormConfirm(@PathVariable int id, @ModelAttribute UserDto user) {
        userService.updateUser(id, user);
        switch (user.getRole()) {
            case "STUDENT" -> studentService.addUserToStudents(id, user);
            case "TEACHER" -> teacherService.addUserToTeacher(id);
            case "PARENT" -> parentService.addUserToParent(id, user);
            case "ADMIN" -> adminService.addUserToAdmin(id);
        }
        return "redirect:/view/admin/user_management";
    }
}



