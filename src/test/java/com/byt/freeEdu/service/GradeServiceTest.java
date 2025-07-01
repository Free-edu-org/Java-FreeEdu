package com.byt.freeEdu.service;

import com.byt.freeEdu.mapper.GradeMapper;
import com.byt.freeEdu.model.DTO.GradeDto;
import com.byt.freeEdu.model.Grade;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.repository.GradeRepository;
import com.byt.freeEdu.repository.TeacherRepository;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
  public void saveGrade_successfullySavesGrade() {
    // given
    GradeDto gradeDto = new GradeDto();
    gradeDto.setSubject("MATH");
    gradeDto.setValue(4.5);

    Grade grade = new Grade();
    when(gradeMapper.toEntity(gradeDto,studentService,teacherService)).thenReturn(grade);
    when(gradeRepository.save(grade)).thenReturn(grade);

    // when
    Boolean result = gradeService.saveGrade(gradeDto);

    // then
    assertTrue(result);
    assertEquals(LocalDate.now(),grade.getGradeDate());
    verify(gradeRepository,times(1)).save(grade);
  }

  @Test
  public void getGradeById_gradeNotFound_throwsException() {
    // given
    int gradeId = 999;
    when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,() -> gradeService.getGradeById(gradeId));
  }

  @Test
  public void getGradeById_returnsGrade() {
    // given
    int gradeId = 1;
    Grade grade = new Grade();
    when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

    // when
    Grade result = gradeService.getGradeById(gradeId);

    // then
    assertNotNull(result);
    verify(gradeRepository,times(1)).findById(gradeId);
  }

  @Test
  public void getGradesByTeacherId_returnsMappedGrades() {
    // given
    int teacherId = 1;
    Grade grade = new Grade();
    GradeDto gradeDto = new GradeDto();

    when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(new Teacher()));
    when(gradeRepository.getGradeByTeacher(any(Teacher.class))).thenReturn(List.of(grade));
    when(gradeMapper.toDto(grade)).thenReturn(gradeDto);

    // when
    List<GradeDto> results = gradeService.getGradesByTeacherId(teacherId);

    // then
    assertNotNull(results);
    assertEquals(1,results.size());
    verify(gradeRepository,times(1)).getGradeByTeacher(any(Teacher.class));
  }

  @Test
  public void getAllGrades_returnsMappedGrades() {
    // given
    Grade grade = new Grade();
    GradeDto gradeDto = new GradeDto();

    when(gradeRepository.findAll()).thenReturn(List.of(grade));
    when(gradeMapper.toDto(grade)).thenReturn(gradeDto);

    // when
    List<GradeDto> results = gradeService.getAllGrades();

    // then
    assertNotNull(results);
    assertEquals(1,results.size());
    verify(gradeRepository,times(1)).findAll();
  }

  @Test
  public void getGradesForStudent_returnsGrades() {
    // given
    int studentId = 1;
    Grade grade = new Grade();

    when(gradeRepository.findByStudentUserId(studentId)).thenReturn(List.of(grade));

    // when
    List<Grade> results = gradeService.getGradesForStudent(studentId);

    // then
    assertNotNull(results);
    assertEquals(1,results.size());
    verify(gradeRepository,times(1)).findByStudentUserId(studentId);
  }

  @Test
  public void updateGrade_successfullyUpdatesGrade() {
    // given
    int gradeId = 1;
    Grade existingGrade = new Grade();
    GradeDto updatedGradeDto = new GradeDto();
    updatedGradeDto.setValue(4.0);

    when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(existingGrade));
    when(gradeRepository.save(existingGrade)).thenReturn(existingGrade);

    // when
    Boolean result = gradeService.updateGrade(gradeId,updatedGradeDto);

    // then
    assertTrue(result);
    assertEquals(4.0,existingGrade.getValue());
    assertEquals(LocalDate.now(),existingGrade.getGradeDate());
    verify(gradeRepository,times(1)).save(existingGrade);
  }

  @Test
  public void deleteGrade_gradeNotFound_throwsException() {
    // given
    int gradeId = 999;
    when(gradeRepository.existsById(gradeId)).thenReturn(false);

    // when & then
    assertThrows(IllegalArgumentException.class,() -> gradeService.deleteGrade(gradeId));
    verify(gradeRepository,never()).deleteById(gradeId);
  }

  @Test
  public void deleteGrade_successfullyDeletesGrade() {
    // given
    int gradeId = 1;
    when(gradeRepository.existsById(gradeId)).thenReturn(true);

    // when
    Boolean result = gradeService.deleteGrade(gradeId);

    // then
    assertTrue(result);
    verify(gradeRepository,times(1)).deleteById(gradeId);
  }
}
