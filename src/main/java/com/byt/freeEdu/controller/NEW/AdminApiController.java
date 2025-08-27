package com.byt.freeEdu.controller.NEW;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.byt.freeEdu.mapper.GradeMapper;
import com.byt.freeEdu.model.DTO.GradeDto;
import com.byt.freeEdu.model.DTO.RemarkDto;
import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.service.GradeService;
import com.byt.freeEdu.service.RemarkService;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;

/**
 * Jeden kontroler REST dla panelu admina (SPA).
 */
@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    // ==== DTO/records do JSON ====
    public record UpdateRemarkRequest(String content) {

    }

    public record RemarkRow(
            Integer id,
            String studentFirstName, String studentLastName,
            String teacherFirstName, String teacherLastName,
            String content,
            String addDate
    ) {

    }

    public record GradeRow(
            Integer gradeId,
            String studentFirstName, String studentLastName,
            String subject,
            Double value,
            String gradeDate,
            String teacherFirstName, String teacherLastName
    ) {}

    public record CreateOrUpdateGradeRequest(
            String subject,
            Double value,                 // <— było Integer
            Integer teacherId,
            Integer studentId
    ) {}

    public record SubjectItem(String name, String displayName) {

    }

    public record SimplePerson(Integer userId, String firstname, String lastname) {

    }

    private final RemarkService remarkService;

    private final GradeService gradeService;

    private final GradeMapper gradeMapper;

    private final TeacherService teacherService;

    private final StudentService studentService;

    public AdminApiController(RemarkService remarkService,
                              GradeService gradeService,
                              GradeMapper gradeMapper,
                              TeacherService teacherService,
                              StudentService studentService) {
        this.remarkService = remarkService;
        this.gradeService = gradeService;
        this.gradeMapper = gradeMapper;
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    // ===== Uwagi =====
    @GetMapping("/remarks")
    public List<RemarkRow> listRemarks() {
        return remarkService.getAllRemarks().stream().map(this::toRemarkRow).toList();
    }

    @PutMapping("/remarks/{id}")
    public ResponseEntity<Void> updateRemark(@PathVariable int id, @RequestBody UpdateRemarkRequest req) {
        RemarkDto dto = remarkService.getAdminRemarkById(id);
        if (dto == null) return ResponseEntity.notFound().build();
        dto.setContent(req.content());
        remarkService.updateRemark(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/remarks/{id}")
    public ResponseEntity<Void> deleteRemark(@PathVariable int id) {
        remarkService.deleteRemark(id);
        return ResponseEntity.noContent().build();
    }

    private RemarkRow toRemarkRow(RemarkDto r) {
        return new RemarkRow(
                r.getRemarkId(),
                r.getStudentFirstName(), r.getStudentLastName(),
                r.getTeacherFirstName(), r.getTeacherLastName(),
                r.getContent(),
                r.getAddDate() != null ? r.getAddDate().toString() : null
        );
    }

    // ===== Słowniki =====
    @GetMapping("/subjects")
    public List<SubjectItem> subjects() {
        return Arrays.stream(SubjectEnum.values())
                .map(s -> new SubjectItem(s.name(), s.getDisplayName() != null ? s.getDisplayName() : s.name()))
                .toList();
    }

    @GetMapping("/teachers")
    public List<SimplePerson> teachers() {
        return teacherService.getAllTeachers().stream()
                .map(t -> new SimplePerson(t.getUserId(), t.getFirstname(), t.getLastname()))
                .toList();
    }

    @GetMapping("/students")
    public List<SimplePerson> students() {
        return studentService.getAllStudents().stream()
                .map(s -> new SimplePerson(s.getUserId(), s.getFirstname(), s.getLastname()))
                .toList();
    }

    // ===== Oceny =====
    @GetMapping("/grades")
    public List<GradeRow> grades() {
        return gradeService.getAllGrades().stream()
                .map(dto -> {
                    return new GradeRow(
                            dto.getGradeId(),
                            dto.getStudentFirstName(), dto.getStudentLastName(),
                            dto.getSubject(),
                            dto.getValue(),                               // <— Double
                            dto.getGradeDate() != null ? dto.getGradeDate().toString() : null,
                            dto.getTeacherFirstName(), dto.getTeacherLastName()
                    );
                })
                .toList();
    }

    @PostMapping("/grades")
    public ResponseEntity<Void> addGrade(@RequestBody CreateOrUpdateGradeRequest req) {
        GradeDto dto = new GradeDto();
        dto.setSubject(req.subject());
        dto.setValue(req.value());        // <— Double
        dto.setTeacherId(req.teacherId());
        dto.setStudentId(req.studentId());
        dto.setGradeDate(LocalDate.now());
        gradeService.saveGrade(dto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/grades/{id}")
    public ResponseEntity<Void> updateGrade(@PathVariable int id,
                                            @RequestBody GradeDto req) {
        GradeDto dto = gradeMapper.toDto(gradeService.getGradeById(id));
        if (dto == null) return ResponseEntity.notFound().build();
        gradeService.updateGrade(id, req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/grades/{id}")
    public ResponseEntity<Void> deleteGrade(@PathVariable int id) {
        gradeService.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
}
