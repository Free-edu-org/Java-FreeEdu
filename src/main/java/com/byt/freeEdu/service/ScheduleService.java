package com.byt.freeEdu.service;

import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.repository.ScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public Schedule addSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public Schedule getScheduleById(int id) {
        return scheduleRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Schedule not found with ID: " + id));
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
