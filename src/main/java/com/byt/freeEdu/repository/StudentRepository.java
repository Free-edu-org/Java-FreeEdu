package com.byt.freeEdu.repository;

import com.byt.freeEdu.model.users.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
}
