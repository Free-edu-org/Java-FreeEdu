package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.users.Teacher;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    @Modifying
    @Transactional
    @Query(value =
            "DELETE FROM admin WHERE id = :id; " +
            "DELETE FROM student WHERE id = :id; " +
            "DELETE FROM parent WHERE id = :id; " +
            "INSERT INTO FreeEduDB.teacher (id) VALUES (:id)", nativeQuery = true)
    void addUserToTeachers(int id);
}
