package com.byt.freeEdu.controller;

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
import com.byt.freeEdu.service.RemarkService;
import com.byt.freeEdu.service.ScheduleService;
import com.byt.freeEdu.service.users.ParentService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parent")
public class ParentApiController{

  private final ParentService parentService;

  private final GradeService gradeService;

  private final GradeMapper gradeMapper;

  private final ScheduleService scheduleService;

  private final ScheduleMapper scheduleMapper;

  private final AttendanceService attendanceService;

  private final AttendanceMapper attendanceMapper;

  private final RemarkService remarkService;

  public ParentApiController(ParentService parentService, GradeService gradeService,
      GradeMapper gradeMapper, ScheduleService scheduleService, ScheduleMapper scheduleMapper,
      AttendanceService attendanceService, AttendanceMapper attendanceMapper,
      RemarkService remarkService) {
    this.parentService = parentService;
    this.gradeService = gradeService;
    this.gradeMapper = gradeMapper;
    this.scheduleService = scheduleService;
    this.scheduleMapper = scheduleMapper;
    this.attendanceService = attendanceService;
    this.attendanceMapper = attendanceMapper;
    this.remarkService = remarkService;
  }

  @GetMapping("/children")
  public Flux<Student> getChildren(@RequestParam int parentId) {
    List<Student> list = parentService.getStudentsByParentId(parentId);
    return Flux.fromIterable(list);
  }

  @GetMapping("/schedule")
  public Flux<ScheduleDto> schedule(@RequestParam int studentId, @RequestParam int parentId) {
    Student student = parentService.getStudentsByParentId(parentId).stream()
        .filter(s -> s.getUserId() == studentId).findFirst()
        .orElseThrow(() -> new RuntimeException("Uczeń nie znaleziony"));

    List<ScheduleDto> list = scheduleService
        .getScheduleByClassId(student.getSchoolClass().getSchoolClassId()).stream()
        .map(scheduleMapper::toDto).collect(Collectors.toList());

    return Flux.fromIterable(list);
  }

  @GetMapping("/grades")
  public Flux<GradeDto> grades(@RequestParam int studentId, @RequestParam int parentId) {
    parentService.getStudentsByParentId(parentId).stream().filter(s -> s.getUserId() == studentId)
        .findFirst().orElseThrow(() -> new RuntimeException("Uczeń nie znaleziony"));

    List<GradeDto> list = gradeService.getGradesForStudent(studentId).stream()
        .map(gradeMapper::toDto).collect(Collectors.toList());

    return Flux.fromIterable(list);
  }

  @GetMapping("/attendance")
  public Flux<AttendanceDto> attendance(@RequestParam int studentId, @RequestParam int parentId) {
    parentService.getStudentsByParentId(parentId).stream().filter(s -> s.getUserId() == studentId)
        .findFirst().orElseThrow(() -> new RuntimeException("Uczeń nie znaleziony"));

    List<Attendance> attendances = attendanceService.getAttendancesForStudent(studentId);
    List<AttendanceDto> list = attendances.stream().map(attendanceMapper::toAttendanceDto)
        .collect(Collectors.toList());

    return Flux.fromIterable(list);
  }

  @GetMapping("/remarks/{studentId}")
  public Flux<RemarkDto> remarks(@PathVariable int studentId, @RequestParam int parentId) {
    parentService.getStudentsByParentId(parentId).stream().filter(s -> s.getUserId() == studentId)
        .findFirst().orElseThrow(() -> new RuntimeException("Uczeń nie znaleziony"));

    List<RemarkDto> list = remarkService.getRemarksByStudentId(studentId);
    return Flux.fromIterable(list);
  }
}
