package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.users.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
}
