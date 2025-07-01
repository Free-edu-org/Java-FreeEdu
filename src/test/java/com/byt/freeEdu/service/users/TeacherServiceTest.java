package com.byt.freeEdu.service.users;

import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.repository.TeacherRepository;
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
class TeacherServiceTest{

  @InjectMocks
  private TeacherService teacherService;

  @Mock
  private TeacherRepository teacherRepository;

  @Test
  public void addTeacher_savesTeacherSuccessfully(){
    // given
    Teacher teacher = new Teacher();
    teacher.setFirstname("John");
    teacher.setLastname("Doe");
    teacher.setEmail("john.doe@example.com");

    when(teacherRepository.save(teacher)).thenReturn(teacher);

    // when
    Teacher result = teacherService.addTeacher(teacher);

    // then
    assertNotNull(result);
    verify(teacherRepository,times(1)).save(teacher);
  }

  @Test
  public void getTeacherById_teacherNotFound_throwsException(){
    // given
    int teacherId = 999;
    when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,() -> teacherService.getTeacherById(teacherId));
    verify(teacherRepository,times(1)).findById(teacherId);
  }

  @Test
  public void getTeacherById_returnsTeacher(){
    // given
    int teacherId = 1;
    Teacher teacher = new Teacher();
    teacher.setFirstname("Alice");

    when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(teacher));

    // when
    Teacher result = teacherService.getTeacherById(teacherId);

    // then
    assertNotNull(result);
    assertEquals("Alice",result.getFirstname());
    verify(teacherRepository,times(1)).findById(teacherId);
  }

  @Test
  public void getAllTeachers_returnsListOfTeachers(){
    // given
    Teacher teacher1 = new Teacher();
    Teacher teacher2 = new Teacher();
    when(teacherRepository.findAll()).thenReturn(List.of(teacher1,teacher2));

    // when
    List<Teacher> result = teacherService.getAllTeachers();

    // then
    assertNotNull(result);
    assertEquals(2,result.size());
    verify(teacherRepository,times(1)).findAll();
  }

  @Test
  public void updateTeacher_teacherNotFound_throwsException(){
    // given
    int teacherId = 999;
    Teacher updatedTeacher = new Teacher();

    when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,
        () -> teacherService.updateTeacher(teacherId,updatedTeacher));
    verify(teacherRepository,never()).save(any(Teacher.class));
  }

  @Test
  public void deleteTeacher_deletesTeacherSuccessfully(){
    // given
    int teacherId = 1;

    // when
    teacherService.deleteTeacher(teacherId);

    // then
    verify(teacherRepository,times(1)).deleteById(teacherId);
  }

  @Test
  public void addUserToTeacher_addsUserSuccessfully(){
    // given
    int userId = 1;

    // when
    teacherService.addUserToTeacher(userId);

    // then
    verify(teacherRepository,times(1)).addUserToTeachers(userId);
  }
}
