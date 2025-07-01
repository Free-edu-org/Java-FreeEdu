package com.byt.freeEdu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.byt.freeEdu.model.Grade;
import com.byt.freeEdu.model.users.Teacher;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer>{

  List<Grade> findByStudentUserId(int studentId);

  List<Grade> getGradeByTeacher(Teacher teacher);
}
