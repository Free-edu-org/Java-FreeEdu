package com.byt.freeEdu.service;

import com.byt.freeEdu.mapper.ScheduleMapper;
import com.byt.freeEdu.model.DTO.ScheduleDto;
import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.repository.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ScheduleMapper scheduleMapper;

    public ScheduleService(ScheduleRepository scheduleRepository, ScheduleMapper scheduleMapper) {
        this.scheduleRepository = scheduleRepository;
        this.scheduleMapper = scheduleMapper;
    }

    public Schedule addSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public List<ScheduleDto> getSchedulesById(int userId) {
        // Pobierz dane z repozytorium
        List<Schedule> schedules = scheduleRepository.findAllById(Collections.singleton(userId));

        // Użyj mappera do konwersji
        return schedules.stream()
                .map(scheduleMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
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
}
