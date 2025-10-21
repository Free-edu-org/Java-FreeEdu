package com.byt.freeEdu.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.byt.freeEdu.mapper.GradeMapper;
import com.byt.freeEdu.model.DTO.GradeDto;
import com.byt.freeEdu.model.Grade;
import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.repository.GradeRepository;
import com.byt.freeEdu.repository.TeacherRepository;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GradeServiceTest{

  @InjectMocks
  private GradeService gradeService;

  @Mock
  private GradeRepository gradeRepository;
  @Mock
  private TeacherRepository teacherRepository;
  @Mock
  private GradeMapper gradeMapper;
  @Mock
  private StudentService studentService;
  @Mock
  private TeacherService teacherService;

  @Test
  @DisplayName("saveGrade: zapisuje ocenę z datą dziś (subject przez String)")
  void saveGrade_successfullySavesGrade_viaStringSubject() {
    GradeDto gradeDto = new GradeDto();
    gradeDto.setSubject("MATH");
    gradeDto.setValue(4.5);

    Grade entity = new Grade();
    when(gradeMapper.toEntity(gradeDto,studentService,teacherService)).thenReturn(entity);
    when(gradeRepository.save(entity)).thenReturn(entity);

    Boolean result = gradeService.saveGrade(gradeDto);

    assertTrue(result);
    assertEquals(LocalDate.now(),entity.getGradeDate());
    assertEquals(SubjectEnum.MATH,entity.getSubject());
    verify(gradeRepository).save(entity);
  }

  @Test
  @DisplayName("saveGrade: zapisuje ocenę gdy podany jest subjectEnum (ignoruje String)")
  void saveGrade_successfullySavesGrade_viaEnumSubject() {
    GradeDto gradeDto = new GradeDto();
    gradeDto.setSubjectEnum(SubjectEnum.MATH);
    gradeDto.setSubject(null);
    gradeDto.setValue(5.0);

    Grade entity = new Grade();
    when(gradeMapper.toEntity(gradeDto,studentService,teacherService)).thenReturn(entity);
    when(gradeRepository.save(entity)).thenReturn(entity);

    Boolean result = gradeService.saveGrade(gradeDto);

    assertTrue(result);
    assertEquals(SubjectEnum.MATH,entity.getSubject());
    verify(gradeRepository).save(entity);
  }

  @Test
  @DisplayName("saveGrade: rzuca IllegalArgumentException, gdy brak informacji o przedmiocie")
  void saveGrade_missingSubject_throws() {
    GradeDto gradeDto = new GradeDto();
    gradeDto.setSubject(null);
    gradeDto.setSubjectEnum(null);

    when(gradeMapper.toEntity(gradeDto,studentService,teacherService)).thenReturn(new Grade());

    assertThrows(IllegalArgumentException.class,() -> gradeService.saveGrade(gradeDto));
    verify(gradeRepository,never()).save(any());
  }

  @Test
  @DisplayName("getGradeById: rzuca EntityNotFoundException jeśli brak encji")
  void getGradeById_gradeNotFound_throwsException() {
    int gradeId = 999;
    when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> gradeService.getGradeById(gradeId));
  }

  @Test
  @DisplayName("getGradeById: zwraca encję")
  void getGradeById_returnsGrade() {
    int gradeId = 1;
    Grade grade = new Grade();
    when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

    Grade result = gradeService.getGradeById(gradeId);

    assertNotNull(result);
    verify(gradeRepository).findById(gradeId);
  }

  @Test
  @DisplayName("getGradesByTeacherId: mapuje encje na DTO")
  void getGradesByTeacherId_returnsMappedGrades() {
    int teacherId = 1;
    Grade grade = new Grade();
    GradeDto dto = new GradeDto();

    when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(new Teacher()));
    when(gradeRepository.getGradeByTeacher(any(Teacher.class))).thenReturn(List.of(grade));
    when(gradeMapper.toDto(grade)).thenReturn(dto);

    List<GradeDto> results = gradeService.getGradesByTeacherId(teacherId);

    assertNotNull(results);
    assertEquals(1,results.size());
    verify(gradeRepository).getGradeByTeacher(any(Teacher.class));
  }

  @Test
  @DisplayName("getGradesByTeacherId: gdy nauczyciel nie istnieje, .get() rzuca NoSuchElementException")
  void getGradesByTeacherId_teacherMissing_throws() {
    when(teacherRepository.findById(123)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class,() -> gradeService.getGradesByTeacherId(123));
    verify(teacherRepository).findById(123);
    verifyNoInteractions(gradeRepository,gradeMapper);
  }

  @Test
  @DisplayName("getAllGrades: mapuje wszystkie encje na DTO")
  void getAllGrades_returnsMappedGrades() {
    Grade grade = new Grade();
    GradeDto dto = new GradeDto();
    when(gradeRepository.findAll()).thenReturn(List.of(grade));
    when(gradeMapper.toDto(grade)).thenReturn(dto);

    List<GradeDto> results = gradeService.getAllGrades();

    assertNotNull(results);
    assertEquals(1,results.size());
    verify(gradeRepository).findAll();
  }

  @Test
  @DisplayName("getGradesForStudent: zwraca listę ocen")
  void getGradesForStudent_returnsGrades() {
    int studentId = 1;
    Grade grade = new Grade();
    when(gradeRepository.findByStudentUserId(studentId)).thenReturn(List.of(grade));

    List<Grade> results = gradeService.getGradesForStudent(studentId);

    assertNotNull(results);
    assertEquals(1,results.size());
    verify(gradeRepository).findByStudentUserId(studentId);
  }

  @Test
  @DisplayName("updateGrade: aktualizuje pola i zapisuje (happy path)")
  void updateGrade_successfullyUpdatesGrade() {
    int gradeId = 1;
    Grade existing = new Grade();

    GradeDto patch = new GradeDto();
    patch.setSubject("MATH");
    patch.setTeacherId(77);
    patch.setStudentId(88);
    patch.setValue(4.0);

    Teacher teacher = new Teacher();
    Student student = new Student();

    when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(existing));
    when(teacherService.getTeacherById(77)).thenReturn(teacher);
    when(studentService.getStudentById(88)).thenReturn(student);
    when(gradeRepository.save(existing)).thenReturn(existing);

    Boolean result = gradeService.updateGrade(gradeId,patch);

    assertTrue(result);
    assertEquals(SubjectEnum.MATH,existing.getSubject());
    assertEquals(teacher,existing.getTeacher());
    assertEquals(student,existing.getStudent());
    assertEquals(4.0,existing.getValue());
    assertEquals(LocalDate.now(),existing.getGradeDate());
    verify(gradeRepository).save(existing);
  }

  @Test
  @DisplayName("updateGrade: gdy ocena nie istnieje, .get() rzuca NoSuchElementException")
  void updateGrade_missingGrade_throws() {
    when(gradeRepository.findById(999)).thenReturn(Optional.empty());

    GradeDto patch = new GradeDto();
    patch.setSubject("MATH");
    assertThrows(NoSuchElementException.class,() -> gradeService.updateGrade(999,patch));

    verify(gradeRepository).findById(999);
    verify(gradeRepository,never()).save(any());
  }

  @Test
  @DisplayName("deleteGrade: gdy brak encji, rzuca IllegalArgumentException")
  void deleteGrade_gradeNotFound_throwsException() {
    int gradeId = 999;
    when(gradeRepository.existsById(gradeId)).thenReturn(false);

    assertThrows(IllegalArgumentException.class,() -> gradeService.deleteGrade(gradeId));
    verify(gradeRepository,never()).deleteById(gradeId);
  }

  @Test
  @DisplayName("deleteGrade: usuwa istniejącą ocenę")
  void deleteGrade_successfullyDeletesGrade() {
    int gradeId = 1;
    when(gradeRepository.existsById(gradeId)).thenReturn(true);

    boolean result = gradeService.deleteGrade(gradeId);

    assertTrue(result);
    verify(gradeRepository).deleteById(gradeId);
  }
}
