package com.byt.freeEdu.controller.OLD;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.byt.freeEdu.controller.OLD.userSesion.SessionService;
import com.byt.freeEdu.mapper.GradeMapper;
import com.byt.freeEdu.mapper.UserMapper;
import com.byt.freeEdu.model.DTO.AttendanceFormDto;
import com.byt.freeEdu.model.DTO.GradeDto;
import com.byt.freeEdu.model.DTO.RemarkDto;
import com.byt.freeEdu.model.DTO.ScheduleDto;
import com.byt.freeEdu.model.DTO.StudentDto;
import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.service.AttendanceService;
import com.byt.freeEdu.service.GradeService;
import com.byt.freeEdu.service.RemarkService;
import com.byt.freeEdu.service.ScheduleService;
import com.byt.freeEdu.service.SchoolClassService;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;
import com.byt.freeEdu.service.users.UserService;

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
  private final SchoolClassService schoolClassService;
  private final AttendanceService attendanceService;

  public ViewControllerTeacher(ScheduleService scheduleService, UserService userService,
                               UserMapper userMapper, GradeMapper gradeMapper, GradeService gradeService,
                               RemarkService remarkService, TeacherService teacherService, SessionService sessionService,
                               StudentService studentService, SchoolClassService schoolClassService,
                               AttendanceService attendanceService) {
    this.scheduleService = scheduleService;
    this.userService = userService;
    this.userMapper = userMapper;
    this.gradeMapper = gradeMapper;
    this.gradeService = gradeService;
    this.remarkService = remarkService;
    this.teacherService = teacherService;
    this.sessionService = sessionService;
    this.studentService = studentService;
    this.schoolClassService = schoolClassService;
    this.attendanceService = attendanceService;
  }

  @GetMapping("/mainpage")
  public String mainpageTeacher(Model model) {
    Integer userId = sessionService.getUserId();
    User user = userService.getUserById(userId);
    UserDto userDto = userMapper.toDto(user);
    model.addAttribute("user", userDto);
    return "teacher/teacher_mainpage";
  }

  // Plan zajęć
  @GetMapping("/schedule")
  public String getScheduleTeacher(Model model) {
    Integer userId = sessionService.getUserId();
    List<ScheduleDto> scheduleDto = scheduleService.getSchedulesByTeacherId(userId);
    model.addAttribute("schedule", scheduleDto);

    Teacher teacher = teacherService.getTeacherById(userId);
    model.addAttribute("teacherId", teacher);

    return "teacher/teacher_schedule";
  }

  // Uwagi
  @GetMapping("/remark")
  public String getRemarksTeacher(Model model) {
    Integer userId = sessionService.getUserId();
    List<RemarkDto> remarksDto = remarkService.getTeacherRemarksById(userId);
    model.addAttribute("remarks", remarksDto);
    return "teacher/teacher_remark";
  }

  @GetMapping("/addRemark")
  public String addRemarkForm(Model model) {
    Integer userId = sessionService.getUserId(); // pozostawione dla spójności, jeśli potrzebne
    List<StudentDto> students = studentService.getAllStudentsDto();
    model.addAttribute("students", students);
    model.addAttribute("remark", new RemarkDto());
    return "teacher/teacher_addRemark";
  }

  @PostMapping("/addRemark")
  public String addRemark(@ModelAttribute RemarkDto remarkDto) {
    Integer teacherId = sessionService.getUserId();
    remarkService.addRemark(remarkDto.getContent(), remarkDto.getStudentId(), teacherId);
    return "redirect:/view/teacher/remark";
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

  @GetMapping("/editRemark/{remarkId}")
  public String editRemarkForm(@PathVariable int remarkId, Model model) {
    Integer userId = sessionService.getUserId();

    RemarkDto remarkDto = remarkService.getRemarkById(remarkId);
    if (remarkDto == null) {
      model.addAttribute("errorMessage", "Nie znaleziono uwagi o podanym ID.");
      return "teacher/teacher_remark";
    }

    Student student = studentService.getStudentById(remarkDto.getStudentId());
    if (student == null) {
      model.addAttribute("errorMessage", "Nie znaleziono ucznia dla podanej uwagi.");
      return "teacher/teacher_remark";
    }

    User user = userService.getUserById(student.getUserId());
    StudentDto studentDto = new StudentDto(user.getUserId(), user.getFirstname(), user.getLastname());

    model.addAttribute("student", studentDto);
    model.addAttribute("remark", remarkDto);
    return "teacher/teacher_editRemark";
  }

  @PostMapping("/editRemark/{remarkId}")
  public String editRemark(@PathVariable int remarkId, @ModelAttribute RemarkDto remarkDto) {
    Integer teacherId = sessionService.getUserId();
    remarkService.updateRemark(remarkId, remarkDto);
    return "redirect:/view/teacher/remark";
  }

  @GetMapping("/grades")
  public String getGrades(Model model) {
    Integer userId = sessionService.getUserId();
    model.addAttribute("grades", gradeService.getGradesByTeacherId(userId));
    return "teacher/teacher_grade";
  }

  @GetMapping("/addGrade")
  public String showAddGradeForm(Model model) {
    Integer userId = sessionService.getUserId();
    model.addAttribute("grade", new GradeDto());
    List<StudentDto> students = studentService.getAllStudentsDto();
    model.addAttribute("students", students);
    model.addAttribute("subjects", SubjectEnum.values());
    model.addAttribute("teacherId", userId);
    return "teacher/teacher_addGrade";
  }

  @PostMapping("/addGrade")
  public String addGrade(@ModelAttribute GradeDto gradeDto, Model model) {
    Integer userId = sessionService.getUserId();
    try {
      gradeDto.setTeacherId(userId);
      gradeDto.setGradeDate(LocalDate.now());
      boolean success = gradeService.saveGrade(gradeDto);

      if (!success) {
        model.addAttribute("error", "Nie udało się dodać oceny. Spróbuj ponownie.");
        return "teacher/add_grade";
      }
      return "redirect:/view/teacher/grades";
    } catch (IllegalArgumentException ex) {
      model.addAttribute("errorMessage", ex.getMessage());
      return "teacher/add_grade";
    }
  }

  @PostMapping("/deleteGrade/{gradeId}")
  public String deleteGrade(@PathVariable int gradeId) {
    Integer userId = sessionService.getUserId();
    gradeService.deleteGrade(gradeId);
    return "redirect:/view/teacher/grades";
  }

  @GetMapping("/editGrade/{gradeId}")
  public String showEditGradeForm(@PathVariable int gradeId, Model model) {
    Integer userId = sessionService.getUserId();
    GradeDto dto = gradeMapper.toDto(gradeService.getGradeById(gradeId));
    if (dto == null) {
      model.addAttribute("errorMessage", "Nie znaleziono oceny o podanym ID.");
      return "teacher/teacher_grade";
    }
    model.addAttribute("grade", dto);
    return "teacher/teacher_editGrade";
  }

  @PostMapping("/editGrade/{gradeId}")
  public String editGrade(@PathVariable int gradeId, @ModelAttribute GradeDto gradeDto, Model model) {
    Integer userId = sessionService.getUserId();
    try {
      boolean success = gradeService.updateGrade(gradeDto.getGradeId(), gradeDto);
      if (!success) {
        model.addAttribute("errorMessage", "Nie udało się zapisać zmian. Spróbuj ponownie.");
        return "teacher/teacher_editGrade";
      }
      return "redirect:/view/teacher/grades";
    } catch (Exception ex) {
      model.addAttribute("errorMessage", "Wystąpił błąd: " + ex.getMessage());
      return "teacher/teacher_editGrade";
    }
  }

  // Obecność
  @GetMapping("/attendance")
  public String attendanceSelect(Model model) {
    Integer userId = sessionService.getUserId();
    List<SchoolClass> schoolClasses = schoolClassService.getAllClassesWithStudentCount();
    model.addAttribute("schoolClasses", schoolClasses);
    return "teacher/teacher_attendanceSelect";
  }

  @GetMapping("/attendance/{classId}")
  public String markAttendance(@PathVariable int classId, @RequestParam String subject, Model model) {
    Integer userId = sessionService.getUserId();

    SchoolClass schoolClass = schoolClassService.getSchoolClassById(classId);
    if (schoolClass == null) {
      model.addAttribute("errorMessage", "Nie znaleziono klasy o podanym ID.");
      return "teacher/teacher_attendanceSelect";
    }

    List<StudentDto> students = studentService.getStudentsBySchoolClassId(classId);
    if (students.isEmpty()) {
      model.addAttribute("errorMessage", "Brak uczniów w wybranej klasie.");
      return "teacher/teacher_attendanceSelect";
    }

    model.addAttribute("schoolClass", schoolClass);
    model.addAttribute("students", students);
    model.addAttribute("selectedSubject", SubjectEnum.valueOf(subject));
    return "teacher/teacher_attendanceMark";
  }

  @PostMapping("/attendance/mark")
  public String saveAttendance(@ModelAttribute AttendanceFormDto attendanceFormDto, Model model) {
    Integer userId = sessionService.getUserId();

    if (attendanceFormDto.getAttendanceMap().isEmpty() || attendanceFormDto.getGlobalSubject() == null) {
      model.addAttribute("errorMessage", "Nie zaznaczono wszystkich wymaganych danych.");
      return "teacher/teacher_attendanceMark";
    }

    attendanceService.markAttendance(
            attendanceFormDto.getAttendanceMap(),
            attendanceFormDto.getGlobalSubject(),
            userId
    );
    return "redirect:/view/teacher/attendance";
  }
}
