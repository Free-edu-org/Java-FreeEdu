package com.byt.freeEdu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.byt.freeEdu.model.users.Student;

import jakarta.transaction.Transactional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer>{

  List<Student> findByParentUserId(int parentId); // Poprawiona metoda

  @Query("SELECT s FROM Student s WHERE s.schoolClass.schoolClassId = :schoolClassId")
  List<Student> getStudentsBySchoolClassId(@Param("schoolClassId") int schoolClassId);

  @Modifying
  @Transactional
  @Query(value = "CALL addUserToStudents(:userId, :classId, :parentId)", nativeQuery = true)
  void addUserToStudents(int userId, int classId, int parentId);
}
