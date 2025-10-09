package com.byt.freeEdu.service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.model.DTO.AttendanceDto;
import com.byt.freeEdu.model.enums.AttendanceEnum;
import com.byt.freeEdu.model.enums.SubjectEnum;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.repository.AttendanceRepository;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest{

  @InjectMocks
  private AttendanceService attendanceService;

  @Mock
  private AttendanceRepository attendanceRepository;
  @Mock
  private StudentService studentService;
  @Mock
  private TeacherService teacherService;

  @Test
  @DisplayName("getAttendancesForStudent: zwraca listę obecności dla ucznia")
  void getAttendancesForStudent_returnsAttendances() {
    int studentId = 1;
    Attendance a = new Attendance();
    when(attendanceRepository.findByStudentUserId(studentId)).thenReturn(List.of(a));

    List<Attendance> out = attendanceService.getAttendancesForStudent(studentId);

    assertNotNull(out);
    assertEquals(1,out.size());
    verify(attendanceRepository).findByStudentUserId(studentId);
    verifyNoMoreInteractions(attendanceRepository,studentService,teacherService);
  }

  @Test
  @DisplayName("saveAttendance: zapisuje obecność")
  void saveAttendance_savesAttendanceSuccessfully() {
    Attendance attendance = new Attendance();
    when(attendanceRepository.save(attendance)).thenReturn(attendance);

    attendanceService.saveAttendance(attendance);

    verify(attendanceRepository).save(attendance);
    verifyNoMoreInteractions(attendanceRepository,studentService,teacherService);
  }

  @Test
  @DisplayName("getAttendanceById: gdy brak, rzuca EntityNotFoundException")
  void getAttendanceById_attendanceNotFound_throwsException() {
    int id = 999;
    when(attendanceRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,() -> attendanceService.getAttendanceById(id));
    verify(attendanceRepository).findById(id);
    verifyNoMoreInteractions(attendanceRepository,studentService,teacherService);
  }

  @Test
  @DisplayName("getAttendanceById: zwraca obecność")
  void getAttendanceById_returnsAttendance() {
    int id = 1;
    Attendance a = new Attendance();
    when(attendanceRepository.findById(id)).thenReturn(Optional.of(a));

    Attendance out = attendanceService.getAttendanceById(id);

    assertNotNull(out);
    verify(attendanceRepository).findById(id);
    verifyNoMoreInteractions(attendanceRepository,studentService,teacherService);
  }

  @Test
  @DisplayName("getAttendanceByIdAdmin: zwraca DTO, gdy istnieje")
  void getAttendanceByIdAdmin_returnsDto() {
    int id = 10;
    Student s = new Student();
    s.setFirstname("Jan");
    s.setLastname("Kowalski");
    Teacher t = new Teacher();
    t.setFirstname("Anna");
    t.setLastname("Nowak");
    Attendance a = new Attendance();
    a.setStudent(s);
    a.setTeacher(t);
    a.setAttendanceDate(LocalDate.now());
    a.setStatus(AttendanceEnum.PRESENT);
    a.setSubject(SubjectEnum.MATH);
    when(attendanceRepository.findById(id)).thenReturn(Optional.of(a));

    AttendanceDto dto = attendanceService.getAttendanceByIdAdmin(id);

    assertNotNull(dto);
    verify(attendanceRepository).findById(id);
    verifyNoMoreInteractions(attendanceRepository,studentService,teacherService);
  }

  @Test
  @DisplayName("getAttendanceByIdAdmin: gdy brak, rzuca IllegalArgumentException")
  void getAttendanceByIdAdmin_notFound_throwsIllegalArgument() {
    int id = 404;
    when(attendanceRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class,() -> attendanceService.getAttendanceByIdAdmin(id));
    verify(attendanceRepository).findById(id);
    verifyNoMoreInteractions(attendanceRepository,studentService,teacherService);
  }

  @Test
  @DisplayName("markAttendance: tworzy i zapisuje obecności dla mapy studentów")
  void markAttendance_savesForEachStudent() {
    int teacherId = 77;
    Map<Integer, AttendanceEnum> map = new LinkedHashMap<>();
    map.put(1,AttendanceEnum.PRESENT);
    map.put(2,AttendanceEnum.ABSENT);

    Student s1 = new Student();
    s1.setUserId(1);
    Student s2 = new Student();
    s2.setUserId(2);
    Teacher t = new Teacher();

    when(studentService.getStudentById(1)).thenReturn(s1);
    when(studentService.getStudentById(2)).thenReturn(s2);
    when(teacherService.getTeacherById(teacherId)).thenReturn(t);

    attendanceService.markAttendance(map,SubjectEnum.MATH,teacherId);

    ArgumentCaptor<Attendance> captor = ArgumentCaptor.forClass(Attendance.class);
    verify(attendanceRepository,times(2)).save(captor.capture());
    List<Attendance> saved = captor.getAllValues();

    assertEquals(2,saved.size());
    assertEquals(s1,saved.get(0).getStudent());
    assertEquals(t,saved.get(0).getTeacher());
    assertEquals(AttendanceEnum.PRESENT,saved.get(0).getStatus());
    assertEquals(SubjectEnum.MATH,saved.get(0).getSubject());
    assertNotNull(saved.get(0).getAttendanceDate());
    assertEquals(s2,saved.get(1).getStudent());
    assertEquals(AttendanceEnum.ABSENT,saved.get(1).getStatus());

    verify(studentService).getStudentById(1);
    verify(studentService).getStudentById(2);
    verify(teacherService,times(2)).getTeacherById(teacherId);
    verifyNoMoreInteractions(attendanceRepository,studentService,teacherService);
  }

  @Test
  @DisplayName("getAllAttendances: zwraca wszystkie obecności")
  void getAllAttendances_returnsAllAttendances() {
    when(attendanceRepository.findAll()).thenReturn(List.of(new Attendance()));

    List<Attendance> out = attendanceService.getAllAttendances();

    assertEquals(1,out.size());
    verify(attendanceRepository).findAll();
    verifyNoMoreInteractions(attendanceRepository,studentService,teacherService);
  }

  @Test
  @DisplayName("getAllAttendancesAdmin: mapuje listę encji do DTO")
  void getAllAttendancesAdmin_returnsDtos() {
    Student s1 = new Student();
    s1.setFirstname("A");
    s1.setLastname("A");
    Teacher t1 = new Teacher();
    t1.setFirstname("T");
    t1.setLastname("T");
    Attendance a1 = new Attendance();
    a1.setStudent(s1);
    a1.setTeacher(t1);
    a1.setStatus(AttendanceEnum.PRESENT);
    a1.setAttendanceDate(LocalDate.now());
    a1.setSubject(SubjectEnum.MATH);

    Student s2 = new Student();
    s2.setFirstname("B");
    s2.setLastname("B");
    Teacher t2 = new Teacher();
    t2.setFirstname("U");
    t2.setLastname("U");
    Attendance a2 = new Attendance();
    a2.setStudent(s2);
    a2.setTeacher(t2);
    a2.setStatus(AttendanceEnum.LATE);
    a2.setAttendanceDate(LocalDate.now());
    a2.setSubject(SubjectEnum.MATH);

    when(attendanceRepository.findAll()).thenReturn(List.of(a1,a2));

    List<AttendanceDto> out = attendanceService.getAllAttendancesAdmin();

    assertNotNull(out);
    assertEquals(2,out.size());
    verify(attendanceRepository).findAll();
    verifyNoMoreInteractions(attendanceRepository,studentService,teacherService);
  }

  @Test
  @DisplayName("updateAttendance: aktualizuje i zapisuje obecność")
  void updateAttendance_updatesAndSaves() {
    int id = 5;
    Attendance existing = new Attendance();
    Attendance updated = new Attendance();

    Student s = new Student();
    s.setUserId(123);
    Teacher t = new Teacher();
    updated.setStudent(s);
    updated.setTeacher(t);
    updated.setAttendanceDate(LocalDate.of(2025,1,2));
    updated.setStatus(AttendanceEnum.LATE);

    when(attendanceRepository.findById(id)).thenReturn(Optional.of(existing));
    when(attendanceRepository.save(any(Attendance.class))).thenAnswer(inv -> inv.getArgument(0));

    Attendance out = attendanceService.updateAttendance(id,updated);

    assertEquals(s,out.getStudent());
    assertEquals(t,out.getTeacher());
    assertEquals(LocalDate.of(2025,1,2),out.getAttendanceDate());
    assertEquals(AttendanceEnum.LATE,out.getStatus());

    verify(attendanceRepository).findById(id);
    verify(attendanceRepository).save(existing);
    verifyNoMoreInteractions(attendanceRepository,studentService,teacherService);
  }

  @Test
  @DisplayName("updateAttendance: gdy brak, rzuca EntityNotFoundException")
  void updateAttendance_notFound_throws() {
    int id = 404;
    when(attendanceRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class,
        () -> attendanceService.updateAttendance(id,new Attendance()));
    verify(attendanceRepository).findById(id);
    verify(attendanceRepository,never()).save(any());
    verifyNoMoreInteractions(attendanceRepository,studentService,teacherService);
  }

  @Test
  @DisplayName("updateAttendanceAdmin: zmienia status, datę, oraz opcjonalnie ucznia/nauczyciela/przedmiot")
  void updateAttendanceAdmin_updatesFields() {
    int id = 9;

    Attendance existing = new Attendance();
    existing.setStatus(AttendanceEnum.ABSENT);
    existing.setAttendanceDate(LocalDate.of(2024,12,31));
    when(attendanceRepository.findById(id)).thenReturn(Optional.of(existing));

    AttendanceDto dto = new AttendanceDto();
    dto.setAttendanceStatus("PRESENT");
    dto.setAttendanceDate("2025-02-03");
    dto.setStudentId(111);
    dto.setTeacherId(222);
    dto.setSubjectEnum(SubjectEnum.MATH);

    Student newStudent = new Student();
    newStudent.setUserId(111);
    Teacher newTeacher = new Teacher();
    when(studentService.getStudentById(111)).thenReturn(newStudent);
    when(teacherService.getTeacherById(222)).thenReturn(newTeacher);

    attendanceService.updateAttendanceAdmin(id,dto);

    assertEquals(AttendanceEnum.PRESENT,existing.getStatus());
    assertEquals(LocalDate.of(2025,2,3),existing.getAttendanceDate());
    assertEquals(newStudent,existing.getStudent());
    assertEquals(newTeacher,existing.getTeacher());
    assertEquals(SubjectEnum.MATH,existing.getSubject());

    verify(attendanceRepository).findById(id);
    verify(studentService).getStudentById(111);
    verify(teacherService).getTeacherById(222);
    verify(attendanceRepository).save(existing);
    verifyNoMoreInteractions(attendanceRepository,studentService,teacherService);
  }

  @Test
  @DisplayName("updateAttendanceAdmin: gdy subjectEnum == null, używa subjectName")
  void updateAttendanceAdmin_usesSubjectNameWhenEnumNull() {
    int id = 12;
    Attendance existing = new Attendance();
    when(attendanceRepository.findById(id)).thenReturn(Optional.of(existing));

    AttendanceDto dto = new AttendanceDto();
    dto.setAttendanceStatus("LATE");
    dto.setAttendanceDate("2025-01-01");
    dto.setStudentId(0);
    dto.setTeacherId(0);
    dto.setSubjectEnum(null);
    dto.setSubjectName("MATH");

    attendanceService.updateAttendanceAdmin(id,dto);

    assertEquals(AttendanceEnum.LATE,existing.getStatus());
    assertEquals(LocalDate.of(2025,1,1),existing.getAttendanceDate());
    assertEquals(SubjectEnum.MATH,existing.getSubject());
    verify(attendanceRepository).findById(id);
    verify(attendanceRepository).save(existing);
    verifyNoInteractions(studentService,teacherService);
    verifyNoMoreInteractions(attendanceRepository);
  }

  @Test
  @DisplayName("updateAttendanceAdmin: gdy brak encji, rzuca IllegalArgumentException")
  void updateAttendanceAdmin_notFound_throwsIllegalArgument() {
    int id = 999;
    when(attendanceRepository.findById(id)).thenReturn(Optional.empty());

    AttendanceDto dto = new AttendanceDto();
    dto.setAttendanceStatus("ABSENT");
    dto.setAttendanceDate("2025-03-10");

    assertThrows(IllegalArgumentException.class,
        () -> attendanceService.updateAttendanceAdmin(id,dto));
    verify(attendanceRepository).findById(id);
    verifyNoMoreInteractions(attendanceRepository);
    verifyNoInteractions(studentService,teacherService);
  }

  @Test
  @DisplayName("deleteAttendance: deleguje do deleteById")
  void deleteAttendance_deletesAttendanceSuccessfully() {
    int id = 1;
    attendanceService.deleteAttendance(id);
    verify(attendanceRepository).deleteById(id);
    verifyNoMoreInteractions(attendanceRepository,studentService,teacherService);
  }
}
