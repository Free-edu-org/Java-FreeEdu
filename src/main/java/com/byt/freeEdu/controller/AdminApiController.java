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
import org.springframework.web.bind.annotation.RestController;

import com.byt.freeEdu.mapper.AttendanceMapper;
import com.byt.freeEdu.mapper.GradeMapper;
import com.byt.freeEdu.mapper.ScheduleMapper;
import com.byt.freeEdu.mapper.UserMapper;
import com.byt.freeEdu.model.DTO.AttendanceDto;
import com.byt.freeEdu.model.DTO.GradeDto;
import com.byt.freeEdu.model.DTO.RemarkDto;
import com.byt.freeEdu.model.DTO.ScheduleAdminDto;
import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.AttendanceService;
import com.byt.freeEdu.service.GradeService;
import com.byt.freeEdu.service.RemarkService;
import com.byt.freeEdu.service.ScheduleService;
import com.byt.freeEdu.service.SchoolClassService;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;
import com.byt.freeEdu.service.users.UserService;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController{

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

  public AdminApiController(RemarkService remarkService, GradeService gradeService,
      GradeMapper gradeMapper, ScheduleService scheduleService, ScheduleMapper scheduleMapper,
      AttendanceService attendanceService, AttendanceMapper attendanceMapper,
      SchoolClassService schoolClassService, UserService userService, UserMapper userMapper,
      TeacherService teacherService, StudentService studentService) {
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
  public List<Map<String, String>> subjects() {
    return Arrays.stream(SubjectEnum.values())
        .map(s -> Map.of("name",s.name(),"displayName",
            s.getDisplayName() != null ? s.getDisplayName() : s.name()))
        .collect(Collectors.toList());
  }

  @GetMapping("/teachers")
  public List<Map<String, Object>> teachers() {
    return teacherService.getAllTeachers().stream().map(t -> Map.<String, Object>of("userId",
        t.getUserId(),"firstname",t.getFirstname(),"lastname",t.getLastname()))
        .collect(Collectors.toList());
  }

  @GetMapping("/students")
  public List<Map<String, Object>> students() {
    return studentService.getAllStudents().stream().map(s -> Map.<String, Object>of("userId",
        s.getUserId(),"firstname",s.getFirstname(),"lastname",s.getLastname()))
        .collect(Collectors.toList());
  }

  /* ===== Uwagi ===== */
  @GetMapping("/remarks")
  public List<Map<String, Object>> listRemarks() {
    return remarkService.getAllRemarks().stream()
        .map(r -> Map.<String, Object>of("id",r.getRemarkId(),"studentFirstName",
            r.getStudentFirstName(),"studentLastName",r.getStudentLastName(),"studentId",
            r.getStudentId(),"teacherFirstName",r.getTeacherFirstName(),"teacherLastName",
            r.getTeacherLastName(),"teacherId",r.getTeacherId(),"content",r.getContent(),"addDate",
            r.getAddDate()))
        .collect(Collectors.toList());
  }

  @PostMapping("/remarks")
  public ResponseEntity<Void> addRemark(@RequestBody Map<String, Object> req) {
    String content = (String) req.get("content");
    Integer studentId = req.get("studentId") != null
        ? ((Number) req.get("studentId")).intValue()
        : null;
    Integer teacherId = req.get("teacherId") != null
        ? ((Number) req.get("teacherId")).intValue()
        : null;
    remarkService.addRemark(content,studentId,teacherId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/remarks/{id}")
  public ResponseEntity<Void> updateRemark(@PathVariable int id, @RequestBody RemarkDto dto) {
    remarkService.updateRemark(id,dto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/remarks/{id}")
  public ResponseEntity<Void> deleteRemark(@PathVariable int id) {
    remarkService.deleteRemark(id);
    return ResponseEntity.noContent().build();
  }

  /* ===== Oceny ===== */
  @GetMapping("/grades")
  public List<Map<String, Object>> grades() {
    return gradeService.getAllGrades().stream().map(dto -> {
      Map<String, Object> map = new java.util.HashMap<>();
      map.put("gradeId",dto.getGradeId());
      map.put("studentFirstName",dto.getStudentFirstName());
      map.put("studentLastName",dto.getStudentLastName());
      map.put("studentId",dto.getStudentId());
      map.put("subject",dto.getSubject());
      map.put("subjectCode",dto.getSubjectEnum() != null ? dto.getSubjectEnum().name() : null);
      map.put("value",dto.getValue());
      map.put("gradeDate",dto.getGradeDate() != null ? dto.getGradeDate().toString() : null);
      map.put("teacherId",dto.getTeacherId());
      map.put("teacherFirstName",dto.getTeacherFirstName());
      map.put("teacherLastName",dto.getTeacherLastName());
      return map;
    }).collect(Collectors.toList());
  }

  @PostMapping("/grades")
  public ResponseEntity<Void> addGrade(@RequestBody GradeDto dto) {
    if (dto.getGradeDate() == null) {
      dto.setGradeDate(LocalDate.now());
    }
    gradeService.saveGrade(dto);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/grades/{id}")
  public ResponseEntity<Void> updateGrade(@PathVariable int id, @RequestBody GradeDto dto) {
    gradeService.updateGrade(id,dto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/grades/{id}")
  public ResponseEntity<Void> deleteGrade(@PathVariable int id) {
    gradeService.deleteGrade(id);
    return ResponseEntity.noContent().build();
  }

  /* ===== Plan ===== */
  @GetMapping("/schedule")
  public List<ScheduleAdminDto> scheduleList() {
    return scheduleService.getAllSchedules();
  }

  @PostMapping("/schedule")
  public ResponseEntity<Void> scheduleAdd(@RequestBody com.byt.freeEdu.model.DTO.ScheduleDto dto) {
    Schedule schedule = new Schedule();
    schedule.setDate(dto.getDate());
    schedule.setSubject(SubjectEnum.valueOf(dto.getSubjectName()));
    schedule.setSchoolClass(
        schoolClassService.getSchoolClassById(Integer.parseInt(dto.getClassName())));
    schedule.setTeacher(teacherService.getTeacherById(dto.getTeacherId()));
    scheduleService.addSchedule(schedule);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/schedule/{id}")
  public ResponseEntity<Void> scheduleUpdate(@PathVariable int id,
      @RequestBody com.byt.freeEdu.model.DTO.ScheduleDto dto) {
    scheduleService.updateSchedule(id,dto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/schedule/{id}")
  public ResponseEntity<Void> scheduleDelete(@PathVariable int id) {
    scheduleService.deleteSchedule(id);
    return ResponseEntity.noContent().build();
  }

  /* ===== Frekwencja ===== */
  @GetMapping("/attendance")
  public List<AttendanceDto> attendanceList() {
    return attendanceService.getAllAttendancesAdmin();
  }

  @GetMapping("/attendance/{id}")
  public AttendanceDto attendanceGet(@PathVariable int id) {
    return attendanceService.getAttendanceByIdAdmin(id);
  }

  @PostMapping("/attendance")
  public ResponseEntity<Void> attendanceAdd(@RequestBody AttendanceDto dto) {
    attendanceService.saveAttendance(dto.toEntity(studentService.getStudentById(dto.getStudentId()),
        teacherService.getTeacherById(dto.getTeacherId())));
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/attendance/{id}")
  public ResponseEntity<Void> attendanceUpdate(@PathVariable int id,
      @RequestBody AttendanceDto dto) {
    attendanceService.updateAttendanceAdmin(id,dto);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/attendance/{id}")
  public ResponseEntity<Void> attendanceDelete(@PathVariable int id) {
    attendanceService.deleteAttendance(id);
    return ResponseEntity.noContent().build();
  }

  /* ===== Klasy ===== */
  @GetMapping("/classes")
  public List<SchoolClass> classesList() {
    return schoolClassService.getAllClassesWithStudentCount();
  }

  @PostMapping("/classes")
  public ResponseEntity<Void> classAdd(@RequestBody String name) {
    schoolClassService.addSchoolClass(name);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/classes/{id}")
  public ResponseEntity<Void> classUpdate(@PathVariable int id, @RequestBody String name) {
    schoolClassService.updateSchoolClass(id,name);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/classes/{id}")
  public ResponseEntity<Void> classDelete(@PathVariable int id) {
    schoolClassService.deleteSchoolClassById(id);
    return ResponseEntity.noContent().build();
  }

  /* ===== Użytkownicy ===== */
  @GetMapping("/users")
  public List<UserDto> usersList() {
    return userService.getAllUsers().stream().map(userMapper::toDto).collect(Collectors.toList());
  }

  @GetMapping("/users/{id}")
  public UserDto getUserById(@PathVariable int id) {
    User userById = userService.getUserById(id);
    return userMapper.toDto(userById);
  }

  @PostMapping("/users")
  public ResponseEntity<Void> userAdd(@RequestBody UserDto user) {
    userService.addUser(user);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/users/{id}")
  public ResponseEntity<Void> userUpdate(@PathVariable int id, @RequestBody UserDto user) {
    userService.updateUser(id,user);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/users/{id}")
  public ResponseEntity<Void> userDelete(@PathVariable int id) {
    userService.deleteUserById(id);
    return ResponseEntity.noContent().build();
  }
}
