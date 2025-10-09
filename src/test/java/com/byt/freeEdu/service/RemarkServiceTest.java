package com.byt.freeEdu.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.byt.freeEdu.mapper.RemarkMapper;
import com.byt.freeEdu.model.DTO.RemarkDto;
import com.byt.freeEdu.model.Remark;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.repository.RemarkRepository;
import com.byt.freeEdu.repository.StudentRepository;
import com.byt.freeEdu.repository.TeacherRepository;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemarkServiceTest{

  @InjectMocks
  private RemarkService remarkService;

  @Mock
  private RemarkRepository remarkRepository;
  @Mock
  private StudentRepository studentRepository;
  @Mock
  private TeacherRepository teacherRepository;
  @Mock
  private RemarkMapper remarkMapper;
  @Mock
  private StudentService studentService;
  @Mock
  private TeacherService teacherService;

  @Test
  void getAllRemarks_returnsMappedRemarkDtos() {
    Remark remark = new Remark("Test content", LocalDate.now(), new Student(), new Teacher());
    when(remarkRepository.findAll()).thenReturn(List.of(remark));
    when(remarkMapper.toDto(any(Remark.class))).thenReturn(new RemarkDto());

    List<RemarkDto> remarks = remarkService.getAllRemarks();

    assertNotNull(remarks);
    assertEquals(1,remarks.size());
    verify(remarkRepository,times(1)).findAll();
  }

  @Test
  void getRemarkById_remarkNotFound_throwsException() {
    int remarkId = 999;
    when(remarkRepository.findById(remarkId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> remarkService.getRemarkById(remarkId));
  }

  @Test
  void getRemarkById_returnsRemarkDto() {
    int remarkId = 1;
    Remark remark = new Remark("Content", LocalDate.now(), new Student(), new Teacher());
    when(remarkRepository.findById(remarkId)).thenReturn(Optional.of(remark));
    when(remarkMapper.toDto(remark)).thenReturn(new RemarkDto());

    RemarkDto result = remarkService.getRemarkById(remarkId);

    assertNotNull(result);
    verify(remarkRepository,times(1)).findById(remarkId);
  }

  @Test
  void getAdminRemarkById_returnsDto() {
    int id = 10;
    Remark remark = new Remark("Admin content", LocalDate.now(), new Student(), new Teacher());
    when(remarkRepository.findById(id)).thenReturn(Optional.of(remark));

    RemarkDto dto = remarkService.getAdminRemarkById(id);

    assertNotNull(dto);
    verify(remarkRepository).findById(id);
  }

  @Test
  void getAdminRemarkById_notFound_throwsIllegalArgument() {
    int id = 404;
    when(remarkRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class,() -> remarkService.getAdminRemarkById(id));
    verify(remarkRepository).findById(id);
  }

  @Test
  void getTeacherRemarksById_teacherNotFound_throwsException() {
    int teacherId = 999;
    when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> remarkService.getTeacherRemarksById(teacherId));
    verify(teacherRepository).findById(teacherId);
    verifyNoInteractions(remarkRepository);
  }

  @Test
  void getTeacherRemarksById_returnsMappedRemarkDtos() {
    int teacherId = 1;
    Teacher teacher = new Teacher();
    teacher.setUserId(teacherId);
    Remark remark = new Remark("T content", LocalDate.now(), new Student(), teacher);
    when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
    when(remarkRepository.findByTeacher(teacher)).thenReturn(List.of(remark));
    when(remarkMapper.toDto(any(Remark.class))).thenReturn(new RemarkDto());

    List<RemarkDto> out = remarkService.getTeacherRemarksById(teacherId);

    assertNotNull(out);
    assertEquals(1,out.size());
    verify(remarkRepository).findByTeacher(teacher);
  }

  @Test
  void getRemarkByContent_returnsRemark() {
    Remark remark = new Remark("X", LocalDate.now(), new Student(), new Teacher());
    when(remarkRepository.findByContent("X")).thenReturn(remark);

    Remark out = remarkService.getRemarkByContent("X");

    assertNotNull(out);
    assertEquals("X",out.getContent());
    verify(remarkRepository).findByContent("X");
  }

  @Test
  void addRemark_emptyContent_throwsException() {
    String content = " ";
    int studentId = 1;
    int teacherId = 1;

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> remarkService.addRemark(content,studentId,teacherId));
    assertEquals("Content cannot be empty",exception.getMessage());
    verify(remarkRepository,never()).save(any());
  }

  @Test
  void addRemark_studentOrTeacherNotFound_throwsException() {
    String content = "Remark content";
    int studentId = 1;
    int teacherId = 1;
    when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> remarkService.addRemark(content,studentId,teacherId));
  }

  @Test
  void addRemark_successfullySavesRemark() {
    String content = "Remark content";
    int studentId = 1;
    int teacherId = 1;
    Student student = new Student();
    student.setUserId(studentId);
    Teacher teacher = new Teacher();
    teacher.setUserId(teacherId);
    Remark remark = new Remark(content, LocalDate.now(), student, teacher);
    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
    when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));
    when(remarkRepository.save(any(Remark.class))).thenReturn(remark);

    Remark result = remarkService.addRemark(content,studentId,teacherId);

    assertNotNull(result);
    assertEquals(content,result.getContent());
    verify(remarkRepository,times(1)).save(any(Remark.class));
  }

  @Test
  void updateRemark_notFound_throwsException() {
    int id = 5;
    when(remarkRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> remarkService.updateRemark(id,new RemarkDto()));
    verify(remarkRepository).findById(id);
    verify(remarkRepository,never()).save(any());
  }

  @Test
  void updateRemark_updatesContentStudentTeacher_whenContentNotEmpty() {
    int id = 7;
    Remark existing = new Remark("old", LocalDate.now(), new Student(), new Teacher());
    RemarkDto dto = new RemarkDto();
    dto.setContent("new");
    dto.setStudentId(111);
    dto.setTeacherId(222);
    Student newStudent = new Student();
    newStudent.setUserId(111);
    Teacher newTeacher = new Teacher();
    newTeacher.setUserId(222);
    when(remarkRepository.findById(id)).thenReturn(Optional.of(existing));
    when(studentService.getStudentById(111)).thenReturn(newStudent);
    when(teacherService.getTeacherById(222)).thenReturn(newTeacher);
    when(remarkRepository.save(existing)).thenReturn(existing);

    Remark out = remarkService.updateRemark(id,dto);

    assertEquals("new",out.getContent());
    assertEquals(newStudent,out.getStudent());
    assertEquals(newTeacher,out.getTeacher());
    verify(remarkRepository).save(existing);
  }

  @Test
  void updateRemark_doesNothing_whenContentEmpty() {
    int id = 8;
    Student originalStudent = new Student();
    originalStudent.setUserId(10);
    Teacher originalTeacher = new Teacher();
    originalTeacher.setUserId(20);
    Remark existing = new Remark("keep", LocalDate.now(), originalStudent, originalTeacher);
    RemarkDto dto = new RemarkDto();
    dto.setContent("  ");
    dto.setStudentId(999);
    dto.setTeacherId(999);
    when(remarkRepository.findById(id)).thenReturn(Optional.of(existing));
    when(remarkRepository.save(existing)).thenReturn(existing);

    Remark out = remarkService.updateRemark(id,dto);

    assertEquals("keep",out.getContent());
    assertEquals(originalStudent,out.getStudent());
    assertEquals(originalTeacher,out.getTeacher());
    verifyNoInteractions(studentService,teacherService);
    verify(remarkRepository).save(existing);
  }

  @Test
  void deleteRemark_remarkNotFound_throwsException() {
    int remarkId = 999;
    when(remarkRepository.findById(remarkId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> remarkService.deleteRemark(remarkId));
    verify(remarkRepository,never()).delete(any());
  }

  @Test
  void deleteRemark_successfullyDeletesRemark() {
    int remarkId = 1;
    Remark remark = new Remark("Test", LocalDate.now(), new Student(), new Teacher());
    when(remarkRepository.findById(remarkId)).thenReturn(Optional.of(remark));

    remarkService.deleteRemark(remarkId);

    verify(remarkRepository,times(1)).deleteById(remarkId);
  }

  @Test
  void getRemarksByStudentId_studentNotFound_throwsException() {
    int studentId = 999;
    when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> remarkService.getRemarksByStudentId(studentId));
  }

  @Test
  void getRemarksByStudentId_returnsMappedRemarkDtos() {
    int studentId = 1;
    Student student = new Student();
    student.setUserId(studentId);
    Remark remark = new Remark("Content", LocalDate.now(), student, new Teacher());
    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
    when(remarkRepository.findByStudent(student)).thenReturn(List.of(remark));
    when(remarkMapper.toDto(any(Remark.class))).thenReturn(new RemarkDto());

    List<RemarkDto> results = remarkService.getRemarksByStudentId(studentId);

    assertNotNull(results);
    assertEquals(1,results.size());
    verify(remarkRepository,times(1)).findByStudent(student);
  }
}
