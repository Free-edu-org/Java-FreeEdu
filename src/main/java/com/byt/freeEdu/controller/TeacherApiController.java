package com.byt.freeEdu.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byt.freeEdu.mapper.GradeMapper;
import com.byt.freeEdu.mapper.ScheduleMapper;
import com.byt.freeEdu.model.DTO.GradeDto;
import com.byt.freeEdu.model.DTO.RemarkDto;
import com.byt.freeEdu.model.DTO.ScheduleDto;
import com.byt.freeEdu.model.DTO.StudentDto;
import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.service.AttendanceService;
import com.byt.freeEdu.service.GradeService;
import com.byt.freeEdu.service.RemarkService;
import com.byt.freeEdu.service.ScheduleService;
import com.byt.freeEdu.service.SchoolClassService;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;

@RestController
@RequestMapping("/api/teacher")
public class TeacherApiController{

  private final ScheduleService scheduleService;

  private final ScheduleMapper scheduleMapper;

  private final GradeService gradeService;

  private final GradeMapper gradeMapper;

  private final RemarkService remarkService;

  private final TeacherService teacherService;

  private final StudentService studentService;

  private final SchoolClassService schoolClassService;

  private final AttendanceService attendanceService;

  public TeacherApiController(ScheduleService scheduleService, ScheduleMapper scheduleMapper,
      GradeService gradeService, GradeMapper gradeMapper, RemarkService remarkService,
      TeacherService teacherService, StudentService studentService,
      SchoolClassService schoolClassService, AttendanceService attendanceService) {
    this.scheduleService = scheduleService;
    this.scheduleMapper = scheduleMapper;
    this.gradeService = gradeService;
    this.gradeMapper = gradeMapper;
    this.remarkService = remarkService;
    this.teacherService = teacherService;
    this.studentService = studentService;
    this.schoolClassService = schoolClassService;
    this.attendanceService = attendanceService;
  }

  /* ====== Profile ====== */
  @GetMapping("/profile")
  public Teacher profile(@RequestParam int teacherId) {
    Teacher t = teacherService.getTeacherById(teacherId);
    if (t == null) {
      throw new RuntimeException("Nauczyciel nie znaleziony");
    }
    return t;
  }

  /* ====== Plan zajęć (Schedule) ====== */
  @GetMapping("/schedule")
  public List<ScheduleDto> schedule(@RequestParam int teacherId) {
    if (teacherService.getTeacherById(teacherId) == null) {
      throw new RuntimeException("Nauczyciel nie znaleziony");
    }

    return scheduleService.getSchedulesByTeacherId(teacherId);
  }

  /* ====== Uwagi (Remarks) ====== */
  @GetMapping("/remarks")
  public List<RemarkDto> remarks(@RequestParam int teacherId) {
    if (teacherService.getTeacherById(teacherId) == null) {
      throw new RuntimeException("Nauczyciel nie znaleziony");
    }

    return remarkService.getTeacherRemarksById(teacherId);
  }

  @PostMapping("/remarks")
  public ResponseEntity<?> addRemark(@RequestParam int teacherId,
      @RequestBody RemarkDto remarkDto) {
    if (teacherService.getTeacherById(teacherId) == null) {
      throw new RuntimeException("Nauczyciel nie znaleziony");
    }
    if (remarkDto == null || remarkDto.getStudentId() == 0 || remarkDto.getContent() == null
        || remarkDto.getContent().isBlank()) {
      return ResponseEntity.badRequest()
          .body(Map.of("status","ERROR","message","Wymagane: studentId, content"));
    }

    remarkService.addRemark(remarkDto.getContent(),remarkDto.getStudentId(),teacherId);
    return ResponseEntity.ok(Map.of("status","OK"));
  }

  @PutMapping("/remarks/{remarkId}")
  public ResponseEntity<?> updateRemark(@RequestParam int teacherId, @PathVariable int remarkId,
      @RequestBody RemarkDto remarkDto) {
    if (teacherService.getTeacherById(teacherId) == null) {
      throw new RuntimeException("Nauczyciel nie znaleziony");
    }

    if (remarkDto != null) {
      remarkDto.setTeacherId(teacherId);
    }

    remarkService.updateRemark(remarkId,remarkDto);
    return ResponseEntity.ok(Map.of("status","OK"));
  }

  @DeleteMapping("/remarks/{remarkId}")
  public ResponseEntity<?> deleteRemark(@RequestParam int teacherId, @PathVariable int remarkId) {
    if (teacherService.getTeacherById(teacherId) == null) {
      throw new RuntimeException("Nauczyciel nie znaleziony");
    }

    remarkService.deleteRemark(remarkId);
    return ResponseEntity.ok(Map.of("status","OK"));
  }

  /* ====== Oceny (Grades) ====== */
  @GetMapping("/grades")
  public List<GradeDto> grades(@RequestParam int teacherId) {
    if (teacherService.getTeacherById(teacherId) == null) {
      throw new RuntimeException("Nauczyciel nie znaleziony");
    }

    return gradeService.getGradesByTeacherId(teacherId);
  }

  @PostMapping("/grades")
  public ResponseEntity<?> addGrade(@RequestParam int teacherId, @RequestBody GradeDto gradeDto) {
    if (teacherService.getTeacherById(teacherId) == null) {
      throw new RuntimeException("Nauczyciel nie znaleziony");
    }

    if (gradeDto == null || gradeDto.getStudentId() == 0 || gradeDto.getSubjectEnum() == null) {
      return ResponseEntity.badRequest()
          .body(Map.of("status","ERROR","message","Wymagane: studentId, subjectEnum, value"));
    }
    gradeDto.setTeacherId(teacherId);
    if (gradeDto.getGradeDate() == null) {
      gradeDto.setGradeDate(LocalDate.now());
    }

    boolean ok = gradeService.saveGrade(gradeDto);
    if (!ok) {
      return ResponseEntity.badRequest()
          .body(Map.of("status","ERROR","message","Nie udało się dodać oceny"));
    }
    return ResponseEntity.ok(Map.of("status","OK"));
  }

  @PutMapping("/grades/{gradeId}")
  public ResponseEntity<?> updateGrade(@RequestParam int teacherId, @PathVariable int gradeId,
      @RequestBody GradeDto gradeDto) {
    if (teacherService.getTeacherById(teacherId) == null) {
      throw new RuntimeException("Nauczyciel nie znaleziony");
    }

    if (gradeDto != null) {
      gradeDto.setTeacherId(teacherId);
    }

    boolean ok = gradeService.updateGrade(gradeId,gradeDto);
    if (!ok) {
      return ResponseEntity.badRequest()
          .body(Map.of("status","ERROR","message","Nie udało się zaktualizować oceny"));
    }
    return ResponseEntity.ok(Map.of("status","OK"));
  }

  @DeleteMapping("/grades/{gradeId}")
  public ResponseEntity<?> deleteGrade(@RequestParam int teacherId, @PathVariable int gradeId) {
    if (teacherService.getTeacherById(teacherId) == null) {
      throw new RuntimeException("Nauczyciel nie znaleziony");
    }

    gradeService.deleteGrade(gradeId);
    return ResponseEntity.ok(Map.of("status","OK"));
  }

  /* ====== Frekwencja (Attendance) ====== */
  @GetMapping("/classes")
  public List<SchoolClass> classes(@RequestParam int teacherId) {
    if (teacherService.getTeacherById(teacherId) == null) {
      throw new RuntimeException("Nauczyciel nie znaleziony");
    }

    return schoolClassService.getAllClassesWithStudentCount();
  }

  @GetMapping("/students")
  public List<StudentDto> studentsByClass(@RequestParam int teacherId, @RequestParam int classId) {
    if (teacherService.getTeacherById(teacherId) == null) {
      throw new RuntimeException("Nauczyciel nie znaleziony");
    }

    return studentService.getStudentsBySchoolClassId(classId);
  }

  @PostMapping("/attendance/mark")
  public ResponseEntity<?> markAttendance(@RequestParam int teacherId,
      @RequestBody com.byt.freeEdu.model.DTO.AttendanceFormDto form) {
    if (teacherService.getTeacherById(teacherId) == null) {
      throw new RuntimeException("Nauczyciel nie znaleziony");
    }

    if (form == null || form.getAttendanceMap() == null || form.getAttendanceMap().isEmpty()
        || form.getGlobalSubject() == null) {
      return ResponseEntity.badRequest()
          .body(Map.of("status","ERROR","message","Wymagane: attendanceMap oraz globalSubject"));
    }

    attendanceService.markAttendance(form.getAttendanceMap(),form.getGlobalSubject(),teacherId);
    return ResponseEntity.ok(Map.of("status","OK"));
  }

  /* ====== Słowniki ====== */
  @GetMapping("/subjects")
  public List<Map<String, String>> subjects() {
    return Arrays.stream(SubjectEnum.values())
        .map(s -> Map.of("name",s.name(),"displayName",
            s.getDisplayName() != null ? s.getDisplayName() : s.name()))
        .collect(Collectors.toList());
  }
}
