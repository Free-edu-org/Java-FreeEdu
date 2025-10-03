package com.byt.freeEdu.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byt.freeEdu.mapper.AttendanceMapper;
import com.byt.freeEdu.mapper.GradeMapper;
import com.byt.freeEdu.mapper.ScheduleMapper;
import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.model.DTO.AttendanceDto;
import com.byt.freeEdu.model.DTO.GradeDto;
import com.byt.freeEdu.model.DTO.RemarkDto;
import com.byt.freeEdu.model.DTO.ScheduleDto;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.service.AttendanceService;
import com.byt.freeEdu.service.GradeService;
import com.byt.freeEdu.service.ScheduleService;
import com.byt.freeEdu.service.users.StudentService;

@RestController
@RequestMapping("/api/student")
public class StudentApiController {

    private final StudentService studentService;

    private final ScheduleService scheduleService;

    private final ScheduleMapper scheduleMapper;

    private final GradeService gradeService;

    private final GradeMapper gradeMapper;

    private final AttendanceService attendanceService;

    private final AttendanceMapper attendanceMapper;

    public StudentApiController(StudentService studentService, ScheduleService scheduleService, ScheduleMapper scheduleMapper, GradeService gradeService, GradeMapper gradeMapper, AttendanceService attendanceService, AttendanceMapper attendanceMapper) {
        this.studentService = studentService;
        this.scheduleService = scheduleService;
        this.scheduleMapper = scheduleMapper;
        this.gradeService = gradeService;
        this.gradeMapper = gradeMapper;
        this.attendanceService = attendanceService;
        this.attendanceMapper = attendanceMapper;
    }

    @GetMapping("/profile")
    public Student profile(@RequestParam int studentId) {
        Student student = studentService.getStudentById(studentId);
        if (student == null) throw new RuntimeException("Uczeń nie znaleziony");
        return student;
    }

    @GetMapping("/schedule")
    public List<ScheduleDto> schedule(@RequestParam int studentId) {
        Student student = studentService.getStudentById(studentId);
        if (student == null) throw new RuntimeException("Uczeń nie znaleziony");

        return scheduleService
                .getScheduleByClassId(student.getSchoolClass().getSchoolClassId()).stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/grades")
    public List<GradeDto> grades(@RequestParam int studentId) {
        // upewnij się, że istnieje
        if (studentService.getStudentById(studentId) == null)
            throw new RuntimeException("Uczeń nie znaleziony");

        return gradeService.getGradesForStudent(studentId).stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/attendance")
    public List<AttendanceDto> attendance(@RequestParam int studentId) {
        if (studentService.getStudentById(studentId) == null)
            throw new RuntimeException("Uczeń nie znaleziony");

        List<Attendance> attendances = attendanceService.getAttendancesForStudent(studentId);
        return attendances.stream()
                .map(attendanceMapper::toAttendanceDto)
                .collect(Collectors.toList());
    }

    /** Jeżeli masz uwagi dla studenta przez StudentService: */
    @GetMapping("/remarks")
    public List<RemarkDto> remarks(@RequestParam int studentId) {
        if (studentService.getStudentById(studentId) == null)
            throw new RuntimeException("Uczeń nie znaleziony");

        return studentService.getRemarksForStudent(studentId);
    }

}
