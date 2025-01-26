package com.byt.freeEdu.service;

import com.byt.freeEdu.mapper.ScheduleMapper;
import com.byt.freeEdu.model.DTO.ScheduleAdminDto;
import com.byt.freeEdu.model.DTO.ScheduleDto;
import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.repository.ScheduleRepository;
import com.byt.freeEdu.service.users.TeacherService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;
    private final TeacherService teacherService;

    public ScheduleService(ScheduleRepository scheduleRepository, ScheduleMapper scheduleMapper, TeacherService teacherService) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
        this.teacherService = teacherService;
    }

    public Schedule addSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public List<ScheduleDto> getSchedulesById(int userId) {
        List<Schedule> schedules = scheduleRepository.findAllById(Collections.singleton(userId));

        return schedules.stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ScheduleDto> getSchedulesByTeacherId(int teacherId) {
        List<Schedule> schedules = scheduleRepository.getAllByTeacher(teacherService.getTeacherById(teacherId));

        return schedules.stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ScheduleAdminDto> getAllSchedules() {
        return scheduleRepository.findAll()
                .stream()
                .map(scheduleMapper::toAdminDto)
                .collect(Collectors.toList());
    }

    public Schedule updateSchedule(int id, Schedule updatedSchedule) {
        Schedule existingSchedule = scheduleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Schedule not found with ID: " + id));
        existingSchedule.setDate(updatedSchedule.getDate());
        existingSchedule.setSubject(updatedSchedule.getSubject());
        existingSchedule.setSchoolClass(updatedSchedule.getSchoolClass());
        existingSchedule.setTeacher(updatedSchedule.getTeacher());
        return scheduleRepository.save(existingSchedule);
    }

    public void deleteSchedule(int id) {
        scheduleRepository.deleteById(id);
    }

    public List<Schedule> getScheduleByClassId(int classId) {
        return scheduleRepository.findBySchoolClass_SchoolClassId(classId);
    }


}
