package com.byt.freeEdu.service;

import com.byt.freeEdu.mapper.GradeMapper;
import com.byt.freeEdu.model.DTO.GradeDto;
import com.byt.freeEdu.model.Grade;
import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.repository.GradeRepository;
import com.byt.freeEdu.repository.TeacherRepository;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradeService {
    private final GradeRepository gradeRepository;
    private final TeacherRepository teacherRepository;
    private final GradeMapper gradeMapper;
    private final StudentService studentService;
    private final TeacherService teacherService;

    public GradeService(GradeRepository gradeRepository, TeacherRepository teacherRepository, GradeMapper gradeMapper, StudentService studentService, TeacherService teacherService) {
        this.gradeRepository = gradeRepository;
        this.teacherRepository = teacherRepository;
        this.gradeMapper = gradeMapper;
        this.studentService = studentService;
        this.teacherService = teacherService;
    }

    public Boolean saveGrade(GradeDto grade) {
        Grade entity = gradeMapper.toEntity(grade, studentService, teacherService);
        entity.setSubject(SubjectEnum.valueOf(grade.getSubject()));
        entity.setGradeDate(LocalDate.now());
        gradeRepository.save(entity);
        return true;
    }

    public Grade getGradeById(int id) {
        return gradeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Grade not found with ID: " + id));
    }

    public List<GradeDto> getGradesByTeacherId(int teacherId) {
        return gradeRepository.getGradeByTeacher(teacherRepository.findById(teacherId).get())
                .stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    public Boolean updateGrade(int id, GradeDto updatedGrade) {
        Grade existingGrade = gradeRepository.findById(id).get();
    public List<Grade> getGradesForStudent(int studentId) {
        return gradeRepository.findByStudent_UserId(studentId);
    }

    public Grade updateGrade(int id, Grade updatedGrade) {
        Grade existingGrade = gradeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Grade not found with ID: " + id));
        existingGrade.setSubject(updatedGrade.getSubject());
        existingGrade.setValue(updatedGrade.getValue());
        existingGrade.setGradeDate(LocalDate.now());

        gradeRepository.save(existingGrade);
        return true;
    }

    public boolean deleteGrade(int id) {
        if (gradeRepository.existsById(id)) {
            gradeRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Ocena o podanym ID nie istnieje.");
        }
        return true;
    }
}
