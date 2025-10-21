package com.byt.freeEdu.service.users;

import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
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
  @DisplayName("addTeacher: zapisuje nauczyciela")
  void addTeacher_savesTeacherSuccessfully() {
    Teacher t = new Teacher();
    t.setFirstname("John");
    t.setLastname("Doe");
    t.setEmail("john@ex.com");
    when(teacherRepository.save(any(Teacher.class))).thenAnswer(inv -> inv.getArgument(0));

    Teacher out = teacherService.addTeacher(t);

    assertNotNull(out);
    assertEquals("John",out.getFirstname());
    verify(teacherRepository).save(any(Teacher.class));
    verifyNoMoreInteractions(teacherRepository);
  }

  @Test
  @DisplayName("getTeacherById: gdy brak, rzuca EntityNotFoundException")
  void getTeacherById_teacherNotFound_throwsException() {
    int id = 999;
    when(teacherRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> teacherService.getTeacherById(id));
    verify(teacherRepository).findById(id);
    verifyNoMoreInteractions(teacherRepository);
  }

  @Test
  @DisplayName("getTeacherById: zwraca nauczyciela")
  void getTeacherById_returnsTeacher() {
    int id = 1;
    Teacher t = new Teacher();
    t.setFirstname("Alice");
    when(teacherRepository.findById(id)).thenReturn(Optional.of(t));

    Teacher out = teacherService.getTeacherById(id);

    assertNotNull(out);
    assertEquals("Alice",out.getFirstname());
    verify(teacherRepository).findById(id);
    verifyNoMoreInteractions(teacherRepository);
  }

  @Test
  @DisplayName("getAllTeachers: zwraca listę nauczycieli")
  void getAllTeachers_returnsListOfTeachers() {
    when(teacherRepository.findAll()).thenReturn(List.of(new Teacher(),new Teacher()));

    List<Teacher> out = teacherService.getAllTeachers();

    assertNotNull(out);
    assertEquals(2,out.size());
    verify(teacherRepository).findAll();
    verifyNoMoreInteractions(teacherRepository);
  }

  @Test
  @DisplayName("updateTeacher: gdy brak, rzuca EntityNotFoundException")
  void updateTeacher_teacherNotFound_throwsException() {
    int id = 999;
    when(teacherRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> teacherService.updateTeacher(id,new Teacher()));
    verify(teacherRepository).findById(id);
    verify(teacherRepository,never()).save(any());
    verifyNoMoreInteractions(teacherRepository);
  }

  @Test
  @DisplayName("updateTeacher: nadpisanie null-ami powoduje NPE (model non-null)")
  void updateTeacher_overwritesWithNulls() {
    int id = 2;
    Teacher existing = new Teacher();
    existing.setUserId(id);
    existing.setFirstname("Old");
    existing.setLastname("Name");
    existing.setEmail("old@ex");
    existing.setPassword("old");

    Teacher patch = new Teacher();

    when(teacherRepository.findById(id)).thenReturn(Optional.of(existing));

    assertThrows(NullPointerException.class,() -> teacherService.updateTeacher(id,patch));
    verify(teacherRepository).findById(id);
    verify(teacherRepository,never()).save(any());
    verifyNoMoreInteractions(teacherRepository);
  }

  @Test
  @DisplayName("updateTeacher: aktualizuje i zapisuje pola")
  void updateTeacher_updatesTeacherSuccessfully() {
    int id = 3;
    Teacher existing = new Teacher();
    existing.setUserId(id);
    existing.setFirstname("Old");
    existing.setLastname("Name");
    existing.setEmail("old@ex");
    existing.setPassword("old");

    Teacher patch = new Teacher();
    patch.setFirstname("New");
    patch.setLastname("Surname");
    patch.setEmail("new@ex");
    patch.setPassword("new");

    when(teacherRepository.findById(id)).thenReturn(Optional.of(existing));
    when(teacherRepository.save(any(Teacher.class))).thenAnswer(inv -> inv.getArgument(0));

    Teacher out = teacherService.updateTeacher(id,patch);

    assertEquals("New",out.getFirstname());
    assertEquals("Surname",out.getLastname());
    assertEquals("new@ex",out.getEmail());
    assertEquals("new",out.getPassword());
    verify(teacherRepository).findById(id);
    verify(teacherRepository).save(any(Teacher.class));
    verifyNoMoreInteractions(teacherRepository);
  }

  @Test
  @DisplayName("deleteTeacher: deleguje do deleteById")
  void deleteTeacher_deletesTeacherSuccessfully() {
    int id = 1;
    teacherService.deleteTeacher(id);
    verify(teacherRepository).deleteById(id);
    verifyNoMoreInteractions(teacherRepository);
  }

  @Test
  @DisplayName("addUserToTeacher: deleguje do repozytorium")
  void addUserToTeacher_addsUserSuccessfully() {
    int userId = 1;
    teacherService.addUserToTeacher(userId);
    verify(teacherRepository).addUserToTeachers(userId);
    verifyNoMoreInteractions(teacherRepository);
  }
}
