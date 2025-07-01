package com.byt.freeEdu.service.users;

import com.byt.freeEdu.mapper.RemarkMapper;
import com.byt.freeEdu.model.DTO.RemarkDto;
import com.byt.freeEdu.model.DTO.StudentDto;
import com.byt.freeEdu.model.DTO.UserDto;
import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.User;
import com.byt.freeEdu.repository.RemarkRepository;
import com.byt.freeEdu.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest{

  @InjectMocks
  private StudentService studentService;

  @Mock
  private RemarkRepository remarkRepository;

  @Mock
  private RemarkMapper remarkMapper;

  @Mock
  private StudentRepository studentRepository;

  @Mock
  private UserService userService;

  @Test
  public void addStudent_savesStudentSuccessfully(){
    // given
    Student student = new Student();
    when(studentRepository.save(student)).thenReturn(student);

    // when
    Student result = studentService.addStudent(student);

    // then
    assertNotNull(result);
    verify(studentRepository,times(1)).save(student);
  }

  @Test
  public void getStudentById_studentNotFound_throwsException(){
    // given
    int studentId = 999;
    when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,() -> studentService.getStudentById(studentId));
  }

  @Test
  public void getStudentById_returnsStudent(){
    // given
    int studentId = 1;
    Student student = new Student();
    when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

    // when
    Student result = studentService.getStudentById(studentId);

    // then
    assertNotNull(result);
    verify(studentRepository,times(1)).findById(studentId);
  }

}
