package com.byt.freeEdu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.byt.freeEdu.model.users.Teacher;

import jakarta.transaction.Transactional;

public interface TeacherRepository extends JpaRepository<Teacher, Integer>{

  @Modifying
  @Transactional
  @Query(value = "CALL addUserToTeachers(:id)", nativeQuery = true)
  void addUserToTeachers(int id);
}
