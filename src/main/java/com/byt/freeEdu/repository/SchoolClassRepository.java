package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Integer> {
    Optional<SchoolClass> findByName(String name);

    @Query("SELECT COUNT(s) FROM Student s WHERE s.schoolClass.schoolClassId = :classId")
    long countStudentsInClass(@Param("classId") int classId);
}
