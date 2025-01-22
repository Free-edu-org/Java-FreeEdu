package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.Remark;
import com.byt.freeEdu.model.users.Student;
import com.byt.freeEdu.model.users.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RemarkRepository extends JpaRepository<Remark, Integer> {
    Remark findByContent(String content);

    List<Remark> findByStudent(Student student);

    List<Remark> getRemarkByTeacher(Teacher teacher);

}
