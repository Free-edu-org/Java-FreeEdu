package com.byt.freeEdu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.byt.freeEdu.model.Remark;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;

@Repository
public interface RemarkRepository extends JpaRepository<Remark, Integer> {

    Remark findByContent(String content);

    List<Remark> findByStudent(Student student);

    List<Remark> getRemarkByTeacher(Teacher teacher);

    List<Remark> findByStudentUserId(int studentId);

    List<Remark> findByTeacher(Teacher teacher);
}
