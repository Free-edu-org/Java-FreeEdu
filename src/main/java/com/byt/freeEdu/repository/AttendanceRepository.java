package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.Attendance;
import com.byt.freeEdu.model.users.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    List<Attendance> findByStudent_UserId(int studentId);
}
