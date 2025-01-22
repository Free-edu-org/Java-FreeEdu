package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.users.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {


    @Query("SELECT s FROM Student s WHERE s.schoolClass.schoolClassId = :schoolClassId")
    List<Student> getStudentsBySchoolClassId(@Param("schoolClassId") int schoolClassId);
}
