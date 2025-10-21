package com.byt.freeEdu.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.byt.freeEdu.mapper.ScheduleMapper;
import com.byt.freeEdu.model.DTO.ScheduleAdminDto;
import com.byt.freeEdu.model.DTO.ScheduleDto;
import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.model.SchoolClass;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.repository.ScheduleRepository;
import com.byt.freeEdu.repository.SchoolClassRepository;
import com.byt.freeEdu.repository.TeacherRepository;
import com.byt.freeEdu.service.users.TeacherService;

import jakarta.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest{

  @InjectMocks
  private ScheduleService scheduleService;

  @Mock
  private ScheduleRepository scheduleRepository;

  @Mock
  private ScheduleMapper scheduleMapper;

  @Mock
  private TeacherService teacherService;

  @Mock
  private TeacherRepository teacherRepository;

  @Mock
  private SchoolClassService schoolClassService;

  @Mock
  private SchoolClassRepository schoolClassRepository;

  @Test
  void addSchedule_successfullySavesSchedule() {
    Schedule schedule = new Schedule();
    when(scheduleRepository.save(schedule)).thenReturn(schedule);
    Schedule result = scheduleService.addSchedule(schedule);
    assertNotNull(result);
    verify(scheduleRepository,times(1)).save(schedule);
  }

  @Test
  void getSchedulesById_returnsMappedSchedules() {
    int userId = 1;
    Schedule schedule = new Schedule();
    ScheduleDto scheduleDto = new ScheduleDto();
    when(scheduleRepository.findAllById(Collections.singleton(userId)))
        .thenReturn(List.of(schedule));
    when(scheduleMapper.toDto(schedule)).thenReturn(scheduleDto);
    List<ScheduleDto> results = scheduleService.getSchedulesById(userId);
    assertNotNull(results);
    assertEquals(1,results.size());
    verify(scheduleRepository,times(1)).findAllById(Collections.singleton(userId));
  }

  @Test
  void getSchedulesByTeacherId_returnsMappedSchedules() {
    int teacherId = 1;
    Teacher teacher = new Teacher();
    Schedule schedule = new Schedule();
    ScheduleDto scheduleDto = new ScheduleDto();
    when(teacherService.getTeacherById(teacherId)).thenReturn(teacher);
    when(scheduleRepository.getAllByTeacher(teacher)).thenReturn(List.of(schedule));
    when(scheduleMapper.toDto(schedule)).thenReturn(scheduleDto);
    List<ScheduleDto> results = scheduleService.getSchedulesByTeacherId(teacherId);
    assertNotNull(results);
    assertEquals(1,results.size());
    verify(scheduleRepository,times(1)).getAllByTeacher(teacher);
  }

  @Test
  void getSchedulesByClassName_returnsMappedSchedules() {
    String className = "1A";
    SchoolClass sc = new SchoolClass();
    sc.setSchoolClassId(100);
    Schedule s = new Schedule();
    ScheduleDto dto = new ScheduleDto();
    when(schoolClassService.getSchoolClassByName(className)).thenReturn(sc);
    when(scheduleRepository.getAllByScheduleId(100)).thenReturn(List.of(s));
    when(scheduleMapper.toDto(s)).thenReturn(dto);
    List<ScheduleDto> out = scheduleService.getSchedulesByClassName(className);
    assertEquals(1,out.size());
    verify(scheduleRepository).getAllByScheduleId(100);
  }

  @Test
  void getAllSchedules_returnsMappedAdminSchedules() {
    Schedule schedule = new Schedule();
    ScheduleAdminDto scheduleAdminDto = new ScheduleAdminDto();
    when(scheduleRepository.findAll()).thenReturn(List.of(schedule));
    when(scheduleMapper.toAdminDto(schedule)).thenReturn(scheduleAdminDto);
    List<ScheduleAdminDto> results = scheduleService.getAllSchedules();
    assertNotNull(results);
    assertEquals(1,results.size());
    verify(scheduleRepository,times(1)).findAll();
  }

  @Test
  void updateSchedule_scheduleNotFound_throwsException() {
    int scheduleId = 1;
    ScheduleDto updated = new ScheduleDto();
    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class,
        () -> scheduleService.updateSchedule(scheduleId,updated));
    verify(scheduleRepository,never()).save(any(Schedule.class));
  }

  @Test
  void updateSchedule_updatesEntityAndRelations() {
    int scheduleId = 5;
    Schedule existing = new Schedule();
    ScheduleDto dto = new ScheduleDto();
    dto.setTeacherId(77);
    dto.setClassName("2B");
    Teacher teacher = new Teacher();
    SchoolClass sc = new SchoolClass();
    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(existing));
    when(teacherRepository.findById(77)).thenReturn(Optional.of(teacher));
    when(schoolClassRepository.findByName("2B")).thenReturn(Optional.of(sc));
    when(scheduleRepository.save(existing)).thenAnswer(inv -> inv.getArgument(0));
    Schedule out = scheduleService.updateSchedule(scheduleId,dto);
    assertNotNull(out);
    assertEquals(teacher,existing.getTeacher());
    assertEquals(sc,existing.getSchoolClass());
    verify(scheduleMapper).updateEntityFromDto(dto,existing);
    verify(scheduleRepository).save(existing);
  }

  @Test
  void updateSchedule_teacherNotFound_throwsException() {
    int scheduleId = 6;
    Schedule existing = new Schedule();
    ScheduleDto dto = new ScheduleDto();
    dto.setTeacherId(123);
    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(existing));
    when(teacherRepository.findById(123)).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class,
        () -> scheduleService.updateSchedule(scheduleId,dto));
    verify(teacherRepository).findById(123);
    verify(scheduleRepository,never()).save(any());
  }

  @Test
  void updateSchedule_classNotFound_throwsException() {
    int scheduleId = 7;
    Schedule existing = new Schedule();
    ScheduleDto dto = new ScheduleDto();
    dto.setClassName("X3");
    when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(existing));
    when(schoolClassRepository.findByName("X3")).thenReturn(Optional.empty());
    assertThrows(EntityNotFoundException.class,
        () -> scheduleService.updateSchedule(scheduleId,dto));
    verify(schoolClassRepository).findByName("X3");
    verify(scheduleRepository,never()).save(any());
  }

  @Test
  void deleteSchedule_successfullyDeletesSchedule() {
    int scheduleId = 1;
    scheduleService.deleteSchedule(scheduleId);
    verify(scheduleRepository,times(1)).deleteById(scheduleId);
  }

  @Test
  void getScheduleByClassId_returnsSchedules() {
    int classId = 1;
    Schedule schedule = new Schedule();
    when(scheduleRepository.findBySchoolClassSchoolClassId(classId)).thenReturn(List.of(schedule));
    List<Schedule> results = scheduleService.getScheduleByClassId(classId);
    assertNotNull(results);
    assertEquals(1,results.size());
    verify(scheduleRepository,times(1)).findBySchoolClassSchoolClassId(classId);
  }
}
