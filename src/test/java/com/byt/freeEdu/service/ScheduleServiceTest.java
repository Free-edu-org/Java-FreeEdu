package com.byt.freeEdu.service;

import com.byt.freeEdu.mapper.ScheduleMapper;
import com.byt.freeEdu.model.DTO.ScheduleAdminDto;
import com.byt.freeEdu.model.DTO.ScheduleDto;
import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.model.users.Teacher;
import com.byt.freeEdu.repository.ScheduleRepository;
import com.byt.freeEdu.service.users.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ScheduleMapper scheduleMapper;

    @Mock
    private TeacherService teacherService;

    @Test
    public void addSchedule_successfullySavesSchedule() {
        //given
        Schedule schedule = new Schedule();
        when(scheduleRepository.save(schedule)).thenReturn(schedule);

        //when
        Schedule result = scheduleService.addSchedule(schedule);

        //then
        assertNotNull(result);
        verify(scheduleRepository, times(1)).save(schedule);
    }

    @Test
    public void getSchedulesById_returnsMappedSchedules() {
        //given
        int userId = 1;
        Schedule schedule = new Schedule();
        ScheduleDto scheduleDto = new ScheduleDto();

        when(scheduleRepository.findAllById(Collections.singleton(userId))).thenReturn(List.of(schedule));
        when(scheduleMapper.toDto(schedule)).thenReturn(scheduleDto);

        //when
        List<ScheduleDto> results = scheduleService.getSchedulesById(userId);

        //then
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(scheduleRepository, times(1)).findAllById(Collections.singleton(userId));
    }

    @Test
    public void getSchedulesByTeacherId_returnsMappedSchedules() {
        //given
        int teacherId = 1;
        Teacher teacher = new Teacher();
        Schedule schedule = new Schedule();
        ScheduleDto scheduleDto = new ScheduleDto();

        when(teacherService.getTeacherById(teacherId)).thenReturn(teacher);
        when(scheduleRepository.getAllByTeacher(teacher)).thenReturn(List.of(schedule));
        when(scheduleMapper.toDto(schedule)).thenReturn(scheduleDto);

        //when
        List<ScheduleDto> results = scheduleService.getSchedulesByTeacherId(teacherId);

        //then
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(scheduleRepository, times(1)).getAllByTeacher(teacher);
    }

    @Test
    public void getAllSchedules_returnsMappedAdminSchedules() {
        //given
        Schedule schedule = new Schedule();
        ScheduleAdminDto scheduleAdminDto = new ScheduleAdminDto();

        when(scheduleRepository.findAll()).thenReturn(List.of(schedule));
        when(scheduleMapper.toAdminDto(schedule)).thenReturn(scheduleAdminDto);

        //when
        List<ScheduleAdminDto> results = scheduleService.getAllSchedules();

        //then
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(scheduleRepository, times(1)).findAll();
    }

    @Test
    public void updateSchedule_scheduleNotFound_throwsException() {
        //given
        int scheduleId = 1;
        Schedule updatedSchedule = new Schedule();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> scheduleService.updateSchedule(scheduleId, updatedSchedule));
        verify(scheduleRepository, never()).save(any(Schedule.class));
    }

    @Test
    public void deleteSchedule_successfullyDeletesSchedule() {
        //given
        int scheduleId = 1;

        //when
        scheduleService.deleteSchedule(scheduleId);

        //then
        verify(scheduleRepository, times(1)).deleteById(scheduleId);
    }

    @Test
    public void getScheduleByClassId_returnsSchedules() {
        //given
        int classId = 1;
        Schedule schedule = new Schedule();

        when(scheduleRepository.findBySchoolClass_SchoolClassId(classId)).thenReturn(List.of(schedule));

        //when
        List<Schedule> results = scheduleService.getScheduleByClassId(classId);

        //then
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(scheduleRepository, times(1)).findBySchoolClass_SchoolClassId(classId);
    }
}
