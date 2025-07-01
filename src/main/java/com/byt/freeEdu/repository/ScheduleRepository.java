package com.byt.freeEdu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.byt.freeEdu.model.Schedule;
import com.byt.freeEdu.model.users.Teacher;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer>{

  List<Schedule> findBySchoolClassSchoolClassId(int schoolClassId);

  List<Schedule> getAllByScheduleId(int scheduleId);

  List<Schedule> getAllByTeacher(Teacher teacher);
}
