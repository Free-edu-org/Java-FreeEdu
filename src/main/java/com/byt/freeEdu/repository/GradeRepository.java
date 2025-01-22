package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.Grade;
import com.byt.freeEdu.model.users.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Integer> {
    List<Grade> getGradeByTeacher(Teacher teacher);
}
