package com.byt.freeEdu.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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
import jakarta.transaction.Transactional;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    private final TeacherRepository teacherRepository;

    private final ScheduleMapper scheduleMapper;

    private final TeacherService teacherService;

    private final SchoolClassService schoolClassService;

    private final SchoolClassRepository schoolClassRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, TeacherRepository teacherRepository, ScheduleMapper scheduleMapper,
                           TeacherService teacherService, SchoolClassService schoolClassService, SchoolClassRepository schoolClassRepository) {
        this.scheduleRepository = scheduleRepository;
        this.teacherRepository = teacherRepository;
        this.scheduleMapper = scheduleMapper;
        this.teacherService = teacherService;
        this.schoolClassService = schoolClassService;
        this.schoolClassRepository = schoolClassRepository;
    }

    public Schedule addSchedule(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public List<ScheduleDto> getSchedulesById(int userId) {
        List<Schedule> schedules = scheduleRepository.findAllById(Collections.singleton(userId));

        return schedules.stream().map(scheduleMapper::toDto).collect(Collectors.toList());
    }

    public List<ScheduleDto> getSchedulesByTeacherId(int teacherId) {
        List<Schedule> schedules = scheduleRepository
                .getAllByTeacher(teacherService.getTeacherById(teacherId));

        return schedules.stream().map(scheduleMapper::toDto).collect(Collectors.toList());
    }

    public List<ScheduleDto> getSchedulesByClassName(String className) {
        SchoolClass schoolClass = schoolClassService.getSchoolClassByName(className);
        List<Schedule> schedules = scheduleRepository
                .getAllByScheduleId(schoolClass.getSchoolClassId());

        return schedules.stream().map(scheduleMapper::toDto).collect(Collectors.toList());
    }

    public List<ScheduleAdminDto> getAllSchedules() {
        return scheduleRepository.findAll().stream().map(scheduleMapper::toAdminDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Schedule updateSchedule(int id, ScheduleDto updatedSchedule) {
        Schedule existing = scheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found with ID: " + id));

        scheduleMapper.updateEntityFromDto(updatedSchedule, existing);

        // relacja: nauczyciel
        if (updatedSchedule.getTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(updatedSchedule.getTeacherId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Teacher not found with userId: " + updatedSchedule.getTeacherId()));
            existing.setTeacher(teacher);
        }

        // relacja: klasa szkolna
        if (updatedSchedule.getClassName() != null && !updatedSchedule.getClassName().trim().isEmpty()) {
            SchoolClass schoolClass = schoolClassRepository.findByName(updatedSchedule.getClassName().trim())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "School class not found: " + updatedSchedule.getClassName()));
            existing.setSchoolClass(schoolClass);
        }

        return scheduleRepository.save(existing);
    }

    public void deleteSchedule(int id) {
        scheduleRepository.deleteById(id);
    }

    public List<Schedule> getScheduleByClassId(int classId) {
        return scheduleRepository.findBySchoolClassSchoolClassId(classId);
    }

}
