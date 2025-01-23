package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.users.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findByParentUserId(int parentId); // Poprawiona metoda
}