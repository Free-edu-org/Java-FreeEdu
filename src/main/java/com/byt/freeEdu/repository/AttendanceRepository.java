package com.byt.freeEdu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.byt.freeEdu.model.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {

    List<Attendance> findByStudentUserId(int studentId);
}
