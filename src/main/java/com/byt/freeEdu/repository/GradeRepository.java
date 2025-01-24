package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.Grade;
import com.byt.freeEdu.model.users.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {
    List<Grade> findByStudent_UserId(int studentId);
    List<Grade> getGradeByTeacher(Teacher teacher);
}
