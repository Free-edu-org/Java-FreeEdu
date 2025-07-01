package com.byt.freeEdu.service;

import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.repository.AttendanceRepository;
import com.byt.freeEdu.service.users.StudentService;
import com.byt.freeEdu.service.users.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

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
  public void getAttendancesForStudent_returnsAttendances(){
    // given
    int studentId = 1;
    Attendance attendance = new Attendance();
    when(attendanceRepository.findByStudentUserId(studentId)).thenReturn(List.of(attendance));

    // when
    List<Attendance> attendances = attendanceService.getAttendancesForStudent(studentId);

    // then
    assertNotNull(attendances);
    assertEquals(1,attendances.size());
    verify(attendanceRepository,times(1)).findByStudentUserId(studentId);
  }

  @Test
  public void saveAttendance_savesAttendanceSuccessfully(){
    // given
    Attendance attendance = new Attendance();
    when(attendanceRepository.save(attendance)).thenReturn(attendance);

    // when
    attendanceService.saveAttendance(attendance);

    // then
    verify(attendanceRepository,times(1)).save(attendance);
  }

  @Test
  public void getAttendanceById_attendanceNotFound_throwsException(){
    // given
    int attendanceId = 999;
    when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(EntityNotFoundException.class,
        () -> attendanceService.getAttendanceById(attendanceId));
  }

  @Test
  public void getAttendanceById_returnsAttendance(){
    // given
    int attendanceId = 1;
    Attendance attendance = new Attendance();
    when(attendanceRepository.findById(attendanceId)).thenReturn(Optional.of(attendance));

    // when
    Attendance result = attendanceService.getAttendanceById(attendanceId);

    // then
    assertNotNull(result);
    verify(attendanceRepository,times(1)).findById(attendanceId);
  }

  @Test
  public void getAllAttendances_returnsAllAttendances(){
    // given
    Attendance attendance = new Attendance();
    when(attendanceRepository.findAll()).thenReturn(List.of(attendance));

    // when
    List<Attendance> results = attendanceService.getAllAttendances();

    // then
    assertNotNull(results);
    assertEquals(1,results.size());
    verify(attendanceRepository,times(1)).findAll();
  }

  @Test
  public void deleteAttendance_deletesAttendanceSuccessfully(){
    // given
    int attendanceId = 1;

    // when
    attendanceService.deleteAttendance(attendanceId);

    // then
    verify(attendanceRepository,times(1)).deleteById(attendanceId);
  }
}
